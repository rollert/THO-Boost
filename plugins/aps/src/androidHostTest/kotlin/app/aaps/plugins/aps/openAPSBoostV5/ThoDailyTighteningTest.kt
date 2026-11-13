package app.aaps.plugins.aps.openAPSBoostV5

import app.aaps.core.keys.DoubleKey
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class ThoDailyTighteningTest {
    private fun suggestion(tbr: Double, severe: Double) = BoostV5AutoConfig.compute(
        BoostV5AutoConfig.V1Profile(4, 1000, 40.0, List(8) { 4.0 }, List(100) { 0.3 },
            tbr, severe, 130.0, 6.0, 5.0))!!
    private val neutral get() = suggestion(2.0, 0.1)
    private val caution get() = suggestion(8.0, 2.5)

    private class Tracking(initial: BoostV5AutoConfig.V5Suggestion) {
        val store = BoostV5AutoConfigApply.managedDoubleKnobs(initial).toMap().toMutableMap()
        private val baseline = store.toMutableMap()
        private val pending = mutableMapOf<DoubleKey, Double>()
        private val written = store.toMutableMap()
        fun run(s: BoostV5AutoConfig.V5Suggestion, tbr: Double, severe: Double) =
            BoostV5AutoConfigApply.redrive(s, tbr, severe,
                storedValue = { store[it] }, baselineValue = { baseline[it] }, pendingValue = { pending[it] },
                put = { k, v -> store[k] = v }, setBaseline = { k, v -> baseline[k] = v },
                setPending = { k, v -> if (v == null) pending.remove(k) else pending[k] = v },
                lastWritten = { written[it] }, recordWritten = { k, v -> written[k] = v }, isOwned = { true })
    }

    @Test fun `first daily adverse window tightens aggression and caution within existing step limits`() {
        val state = Tracking(neutral)
        state.run(caution, 8.0, 2.5)
        assertThat(state.store[DoubleKey.ApsBoostV5Aggression]).isEqualTo(0.85)
        assertThat(state.store[DoubleKey.ApsBoostV5HypoCaution]).isEqualTo(1.25)
    }

    @Test fun `a better next day does not immediately undo the previous tightening`() {
        val state = Tracking(neutral)
        state.run(caution, 8.0, 2.5)
        state.run(neutral, 2.0, 0.1)
        assertThat(state.store[DoubleKey.ApsBoostV5Aggression]).isEqualTo(0.85)
        assertThat(state.store[DoubleKey.ApsBoostV5HypoCaution]).isEqualTo(1.25)
    }

    @Test fun `loosening still needs a repeated daily result`() {
        val state = Tracking(caution)
        state.run(neutral, 2.0, 0.1)
        assertThat(state.store[DoubleKey.ApsBoostV5Aggression]).isEqualTo(0.85)
        assertThat(state.store[DoubleKey.ApsBoostV5HypoCaution]).isEqualTo(2.0)
        state.run(neutral, 2.0, 0.1)
        assertThat(state.store[DoubleKey.ApsBoostV5Aggression]).isEqualTo(1.0)
        assertThat(state.store[DoubleKey.ApsBoostV5HypoCaution]).isEqualTo(1.5)
    }

    @Test fun `a repeated loosening is still held by the TBR guard`() {
        val state = Tracking(caution)
        state.run(neutral, 8.0, 2.5)
        state.run(neutral, 8.0, 2.5)
        assertThat(state.store[DoubleKey.ApsBoostV5Aggression]).isEqualTo(0.85)
        assertThat(state.store[DoubleKey.ApsBoostV5HypoCaution]).isEqualTo(2.0)
    }
}
