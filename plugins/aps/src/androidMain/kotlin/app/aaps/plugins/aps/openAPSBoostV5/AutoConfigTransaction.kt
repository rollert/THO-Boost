package app.aaps.plugins.aps.openAPSBoostV5

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal data class AutoConfigSnapshot(
    val baselines: Map<String, Double>,
    val pending: Map<String, Double>,
    val written: Map<String, Double>
)

/** Eligibility and tracking state consumed by a re-derivation. */
internal suspend fun <T : Any> withCurrentAutoConfig(
    lock: Mutex,
    allResolved: () -> Boolean,
    snapshot: () -> AutoConfigSnapshot,
    evaluate: (AutoConfigSnapshot) -> T
): T? {
    val eligible = allResolved()
    val state = snapshot()
    return lock.withLock { if (eligible) evaluate(state) else null }
}
