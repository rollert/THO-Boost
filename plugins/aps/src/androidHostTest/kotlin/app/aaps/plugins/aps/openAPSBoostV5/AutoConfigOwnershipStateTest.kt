package app.aaps.plugins.aps.openAPSBoostV5

import app.aaps.core.keys.BooleanComposedKey
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.BooleanComposedNonPreferenceKey
import app.aaps.core.keys.interfaces.ComposedKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.keys.interfaces.StringNonPreferenceKey
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock

class AutoConfigOwnershipStateTest {
    private class Store {
        val flags = mutableMapOf<String, Boolean>()
        val strings = mutableMapOf<String, String>()
        private fun path(key: ComposedKey, args: Array<out Any>) = key.key + args.single()
        val preferences = object : Preferences by mock<Preferences>() {
            override fun get(key: BooleanComposedNonPreferenceKey, vararg arguments: Any) = flags[path(key, arguments)] ?: key.defaultValue
            override fun put(key: BooleanComposedNonPreferenceKey, vararg arguments: Any, value: Boolean) { flags[path(key, arguments)] = value }
            override fun remove(key: ComposedKey, vararg arguments: Any) { flags.remove(path(key, arguments)) }
            override fun getIfExists(key: StringNonPreferenceKey) = strings[key.key]
            override fun put(key: StringNonPreferenceKey, value: String) { strings[key.key] = value }
        }
    }

    @Test fun `return to auto reopens only that key and clears stale tracking before next cycle`() {
        val store = Store()
        val key = DoubleKey.ApsBoostV5CommittedCapU
        store.flags[BooleanComposedKey.BoostV5AutoConfigResolved.key + key.key] = true
        store.strings[StringKey.ApsBoostV5RedriveBaseline.key] = """{"${key.key}":2.0,"unrelated":7.0}"""
        store.strings[StringKey.ApsBoostV5AutoConfigPending.key] = """{"${key.key}":1.2}"""
        var retry = false
        val ownership = AutoConfigOwnership(store.preferences) { retry = true }
        ownership.setAuto(key, true)
        assertThat(ownership.isAuto(key)).isTrue()
        assertThat(store.flags[BooleanComposedKey.BoostV5AutoConfigResolved.key + key.key]).isNull()
        assertThat(AutoConfigLedger.read(store.preferences, StringKey.ApsBoostV5RedriveBaseline)).containsExactly("unrelated", 7.0)
        assertThat(AutoConfigLedger.read(store.preferences, StringKey.ApsBoostV5AutoConfigPending)).isEmpty()
        assertThat(retry).isTrue()
        assertThat(AutoConfigOwnership(store.preferences) {}.isAuto(key)).isTrue()
    }

    @Test fun `manual takeover persists across restart and marks the key resolved`() {
        val store = Store()
        val key = DoubleKey.ApsBoostV5CommittedCapU
        var retry = false
        val ownership = AutoConfigOwnership(store.preferences) { retry = true }
        ownership.setAuto(key, false)
        assertThat(AutoConfigOwnership(store.preferences) {}.isAuto(key)).isFalse()
        assertThat(store.flags[BooleanComposedKey.BoostV5AutoConfigResolved.key + key.key]).isTrue()
        assertThat(retry).isFalse()
    }
}
