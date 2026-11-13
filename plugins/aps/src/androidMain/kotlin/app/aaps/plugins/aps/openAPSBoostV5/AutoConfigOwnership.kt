package app.aaps.plugins.aps.openAPSBoostV5

import app.aaps.core.keys.BooleanComposedKey
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.Preferences

/** Persisted THO Auto switches. Caller serializes mutations with the engine's config lock. */
internal class AutoConfigOwnership(private val preferences: Preferences, private val retryOnboarding: () -> Unit) {
    fun isAuto(key: DoubleKey): Boolean = preferences.get(BooleanComposedKey.BoostV5AutoConfigOwned, key.key)

    fun setAuto(key: DoubleKey, enabled: Boolean) {
        preferences.put(BooleanComposedKey.BoostV5AutoConfigOwned, key.key, value = enabled)
        if (enabled) {
            preferences.remove(BooleanComposedKey.BoostV5AutoConfigResolved, key.key)
            // Avoid applying an old baseline's movement immediately after an explicit fresh derivation.
            AutoConfigLedger.put(preferences, StringKey.ApsBoostV5RedriveBaseline, key.key, null)
            AutoConfigLedger.put(preferences, StringKey.ApsBoostV5AutoConfigPending, key.key, null)
            retryOnboarding()
        } else preferences.put(BooleanComposedKey.BoostV5AutoConfigResolved, key.key, value = true)
    }
}
