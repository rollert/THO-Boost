package app.aaps.plugins.aps.openAPSBoostV5

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import org.junit.jupiter.api.Test

class AutoConfigTransactionTest {
    @Test fun `queued redrive must recheck eligibility after a return to auto`() = runBlocking {
        val lock = Mutex(locked = true)
        var resolved = true
        var applied = false
        val baseline = mutableMapOf("cap" to 42.0)
        val job = async(start = CoroutineStart.UNDISPATCHED) {
            withCurrentAutoConfig(lock, { resolved }, { AutoConfigSnapshot(baseline.toMap(), emptyMap(), emptyMap()) }) {
                applied = true
                it.baselines["cap"] ?: 0.0
            }
        }
        resolved = false
        baseline.clear()
        lock.unlock()
        assertThat(job.await()).isNull()
        assertThat(applied).isFalse()
    }

    @Test fun `queued redrive consumes tracking from inside the transaction not an earlier snapshot`() = runBlocking {
        val lock = Mutex(locked = true)
        val baseline = mutableMapOf("cap" to 42.0)
        val job = async(start = CoroutineStart.UNDISPATCHED) {
            withCurrentAutoConfig(lock, { true }, { AutoConfigSnapshot(baseline.toMap(), emptyMap(), emptyMap()) }) {
                it.baselines["cap"] ?: 0.0
            }
        }
        baseline.clear()
        lock.unlock()
        assertThat(job.await()).isEqualTo(0.0)
    }
}
