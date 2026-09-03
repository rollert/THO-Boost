package app.aaps.plugins.aps.openAPSBoostV5

import app.aaps.core.keys.DoubleKey
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

/**
 * 2026-09 override workflow — per-knob auto-config OWNERSHIP.
 *
 * Semantics:
 *  - applyAutoConfig: `isUserTuned` no longer blocks an OWNED knob — the user's explicit
 *    "Auto" switch hands the value back to the derivation. All write-side guards (raise-guard)
 *    still apply. Default isOwned = { false } keeps historical behaviour bit-identical.
 *  - redrive: knobs reported !owned (only when the caller supplies isOwned) surface
 *    MANUAL_FROZEN and are skipped; no baseline churn, nothing written.
 */
class BoostV5AutoConfigOwnershipTest {

    private val safeTbr = 2.0   // below both raise-guard lines
    private val CCAP = DoubleKey.ApsBoostV5CommittedCapU
    private val FCAP = DoubleKey.ApsBoostV5ConfirmedCapU

    private val suggestion = BoostV5AutoConfig.V5Suggestion(
        aggression = 1.0, hypoCaution = 1.0,
        confirmedCapU = 4.5, committedCapU = 1.5,
        cumulativeSmbCap60MinU = 7.5, maxIobU = 8.0, bolusCapU = 10.0,
        fastCarbConfirm = true, aggressiveEarlyConfirm = false, velocityBudgetFloor = false,
        primerCapU = 0.9, primerTbrFallback = true, rationale = listOf("test")
    )

    // ── applyAutoConfig ────────────────────────────────────────────────────────────────────────

    @Test
    fun `owned knob overrides a user-tuned value when returned to auto`() {
        val store = mutableMapOf(CCAP to 0.33)
        val resolved = mutableSetOf<DoubleKey>()
        val owned = mutableSetOf(CCAP)
        val res = BoostV5AutoConfigApply.applyAutoConfig(
            suggestion, tbrBelow70Pct = safeTbr,
            isResolved = { it in resolved },
            isOwned = { it in owned },
            storedValue = { store[it] },
            put = { k, v -> store[k] = v },
            markResolved = { resolved += it }
        )
        val committed = res.first { it.key == CCAP }
        assertThat(committed.outcome).isEqualTo(BoostV5AutoConfigApply.Outcome.APPLIED)
        assertThat(store[CCAP]).isEqualTo(1.5)
    }

    @Test
    fun `owned knob is still raise-guarded`() {
        val store = mutableMapOf(CCAP to 0.33)
        val resolved = mutableSetOf<DoubleKey>()
        val owned = mutableSetOf(CCAP)
        val raised = suggestion.copy(committedCapU = 2.0)
        val res = BoostV5AutoConfigApply.applyAutoConfig(
            raised, tbrBelow70Pct = 6.0,          // above the 4% raise-guard line
            isResolved = { it in resolved },
            isOwned = { it in owned },
            storedValue = { store[it] },
            put = { k, v -> store[k] = v },
            markResolved = { resolved += it }
        )
        assertThat(res.first { it.key == CCAP }.outcome)
            .isEqualTo(BoostV5AutoConfigApply.Outcome.SUGGESTED_NOT_APPLIED_TBR)
        assertThat(store[CCAP]).isEqualTo(0.33)
    }

    @Test
    fun `non-owned tuned knob is still kept - default is bit-identical`() {
        val store = mutableMapOf(CCAP to 0.33)
        val resolved = mutableSetOf<DoubleKey>()
        val res = BoostV5AutoConfigApply.applyAutoConfig(
            suggestion, tbrBelow70Pct = safeTbr,
            isResolved = { it in resolved },
            storedValue = { store[it] },
            put = { k, v -> store[k] = v },
            markResolved = { resolved += it }
        )
        assertThat(res.first { it.key == CCAP }.outcome)
            .isEqualTo(BoostV5AutoConfigApply.Outcome.KEPT_USER_TUNED)
        assertThat(store[CCAP]).isEqualTo(0.33)
    }

    // ── redrive ────────────────────────────────────────────────────────────────────────────────

    @Test
    fun `redrive skips a knob reported not owned - MANUAL_FROZEN, nothing written for that key`() {
        val store = mutableMapOf(FCAP to 3.0)
        val baseline = mutableMapOf(FCAP to 3.0)
        val pending = mutableMapOf<DoubleKey, Double>()
        val writes = mutableListOf<Pair<DoubleKey, Double>>()
        val res = BoostV5AutoConfigApply.redrive(
            suggestion, safeTbr, 0.1,
            isOwned = { false },
            storedValue = { store[it] },
            baselineValue = { baseline[it] },
            pendingValue = { pending[it] },
            put = { k, v -> writes += k to v; store[k] = v },
            setBaseline = { k, v -> baseline[k] = v },
            setPending = { k, v -> if (v == null) pending.remove(k) else pending[k] = v }
        )
        val confirmed = res.first { it.key == FCAP }
        assertThat(confirmed.outcome).isEqualTo(BoostV5AutoConfigApply.Outcome.MANUAL_FROZEN)
        // The frozen KEY is untouched. Other computed knobs (cumulative, primer) may still write
        // — they are separate keys with their own ownership state.
        assertThat(writes.map { it.first }).doesNotContain(FCAP)
        assertThat(store[FCAP]).isEqualTo(3.0)
    }

    @Test
    fun `redrive with no isOwned supplied behaves exactly as before`() {
        val store = mutableMapOf(FCAP to 3.0)
        val baseline = mutableMapOf(FCAP to 3.0)
        val pending = mutableMapOf<DoubleKey, Double>()
        // The legacy run first records a baseline for every key, then tracks movement. Seed the
        // baseline the way the first legacy run would have, then verify the second run tracks.
        BoostV5AutoConfigApply.redrive(
            suggestion, safeTbr, 0.1,
            storedValue = { store[it] },
            baselineValue = { baseline[it] },
            pendingValue = { pending[it] },
            put = { k, v -> store[k] = v },
            setBaseline = { k, v -> baseline[k] = v },
            setPending = { k, v -> if (v == null) pending.remove(k) else pending[k] = v }
        )
        // Second run, UNCHANGED derivation: the committed cap follows the same TDD/40 ratio
        // logic — since stored(FCAP)=3.0 sits above derived, the ratio-tracking moves it toward
        // derived (that IS the legacy behaviour). The point of this test is only that legacy
        // behaviour is unchanged when no isOwned is supplied — assert FCAP still tracked/written
        // exactly as the pre-ownership build did (i.e. NOT frozen).
        val res2 = BoostV5AutoConfigApply.redrive(
            suggestion, safeTbr, 0.1,
            storedValue = { store[it] },
            baselineValue = { baseline[it] },
            pendingValue = { pending[it] },
            put = { k, v -> store[k] = v },
            setBaseline = { k, v -> baseline[k] = v },
            setPending = { k, v -> if (v == null) pending.remove(k) else pending[k] = v }
        )
        assertThat(res2.first { it.key == FCAP }.outcome)
            .isNotEqualTo(BoostV5AutoConfigApply.Outcome.MANUAL_FROZEN)
    }
}
