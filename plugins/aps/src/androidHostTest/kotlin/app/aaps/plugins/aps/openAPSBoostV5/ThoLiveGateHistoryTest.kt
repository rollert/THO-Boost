package app.aaps.plugins.aps.openAPSBoostV5

import app.aaps.core.data.model.GV
import app.aaps.core.data.model.SourceSensor
import app.aaps.core.data.model.TrendArrow
import app.aaps.core.interfaces.db.PersistenceLayer
import app.aaps.plugins.aps.openAPSBoost.OpenAPSBoostPlugin
import app.aaps.shared.tests.TestBaseWithProfile
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ThoLiveGateHistoryTest : TestBaseWithProfile() {
    @Test fun `live gate retains older lows even when the auto-config window is four days`() = runBlocking {
        val now = 1_800_000_000_000L
        val day = 86_400_000L
        fun reading(ageDays: Int, value: Double) = GV(timestamp = now - ageDays * day,
            raw = null, value = value, trendArrow = TrendArrow.NONE, noise = null, sourceSensor = SourceSensor.DEXCOM_G6)
        val history = List(1200) { reading(1, 120.0) } + List(200) { reading(7, 50.0) }
        val persistence = mock<PersistenceLayer>()
        var requestedStart = 0L
        whenever(persistence.getBgReadingsDataFromTimeToTime(any(), eq(now), eq(true))).thenAnswer {
            requestedStart = it.getArgument(0)
            history.filter { bg -> bg.timestamp >= requestedStart }
        }
        val engine = mock<OpenAPSBoostPlugin>()
        val plugin = OpenAPSBoostV5Plugin(aapsLogger, rh, config, preferences, mock(), lazy { engine },
            persistence, mock(), mock(), dateUtil, notificationManager, rxBus, activePlugin, mock())
        assertThat(plugin.composedFloorTbrAllowed(now)).isFalse()
        assertThat(requestedStart).isEqualTo(now - 14 * day)
    }
}
