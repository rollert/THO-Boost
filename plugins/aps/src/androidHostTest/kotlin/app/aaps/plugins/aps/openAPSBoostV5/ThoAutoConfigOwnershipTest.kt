package app.aaps.plugins.aps.openAPSBoostV5

import app.aaps.core.keys.DoubleKey
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class ThoAutoConfigOwnershipTest {
    private val committed = DoubleKey.ApsBoostV5CommittedCapU
    private val confirmed = DoubleKey.ApsBoostV5ConfirmedCapU
    private val cumulative = DoubleKey.ApsBoostCumulativeSmbCap60Min
    private val primer = DoubleKey.ApsBoostV5PrimerCapU
    private val suggestion get() = BoostV5AutoConfig.compute(BoostV5AutoConfig.V1Profile(
        4, 1000, 40.0, List(8) { 4.0 }, List(100) { 0.3 }, 2.0, 0.1, 130.0, 6.0, 5.0))!!

    private fun apply(store: MutableMap<DoubleKey, Double>, auto: Boolean, tbr: Double = 2.0) =
        BoostV5AutoConfigApply.applyAutoConfig(suggestion, tbr, isResolved = { false },
            storedValue = { store[it] }, put = { k, v -> store[k] = v }, markResolved = {},
            isOwned = { it == committed && auto })

    @Test fun `returning a user tuned cap to auto applies derivation`() {
        val store = mutableMapOf(committed to 0.33)
        val result = apply(store, auto = true)
        assertThat(store[committed]).isEqualTo(1.0)
        assertThat(result.first { it.key == committed }.outcome).isEqualTo(BoostV5AutoConfigApply.Outcome.APPLIED)
    }

    @Test fun `auto ownership does not bypass the TBR raise guard`() {
        val store = mutableMapOf(committed to 0.33)
        val result = apply(store, auto = true, tbr = 8.0)
        assertThat(store[committed]).isEqualTo(0.33)
        assertThat(result.first { it.key == committed }.outcome).isEqualTo(BoostV5AutoConfigApply.Outcome.SUGGESTED_NOT_APPLIED_TBR)
    }

    @Test fun `a manual value remains unchanged when not returned to auto`() {
        val store = mutableMapOf(committed to 0.33)
        assertThat(apply(store, auto = false).first { it.key == committed }.outcome)
            .isEqualTo(BoostV5AutoConfigApply.Outcome.KEPT_USER_TUNED)
        assertThat(store[committed]).isEqualTo(0.33)
    }

    @Test fun `manual tracked and computed caps cannot move or advance their baselines`() {
        val store = mutableMapOf(committed to 1.0, confirmed to 4.0, cumulative to 8.0, primer to 0.5)
        val baselines = mutableMapOf(confirmed to 2.0)
        val result = BoostV5AutoConfigApply.redrive(suggestion, 2.0, 0.1,
            storedValue = { store[it] }, baselineValue = { baselines[it] }, pendingValue = { null },
            put = { k, v -> store[k] = v }, setBaseline = { k, v -> baselines[k] = v },
            setPending = { _, _ -> }, lastWritten = { store[it] }, isOwned = { false })
        assertThat(store[confirmed]).isEqualTo(4.0)
        assertThat(store[cumulative]).isEqualTo(8.0)
        assertThat(store[primer]).isEqualTo(0.5)
        assertThat(baselines).containsExactly(confirmed, 2.0)
        for (key in listOf(confirmed, committed, cumulative, primer))
            assertThat(result.first { it.key == key }.outcome).isEqualTo(BoostV5AutoConfigApply.Outcome.MANUAL_FROZEN)
    }
}
