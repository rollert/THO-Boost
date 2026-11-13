package app.aaps.plugins.constraints.dstHelper

import app.aaps.core.interfaces.aps.Loop
import app.aaps.shared.tests.TestBaseWithProfile
import com.google.common.truth.Truth.assertWithMessage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import java.time.Instant
import java.util.Calendar
import java.util.TimeZone

class ThoDstWindowTest : TestBaseWithProfile() {
    @Mock lateinit var loop: Loop
    private lateinit var plugin: DstHelperPlugin

    @BeforeEach fun prepare() {
        plugin = DstHelperPlugin(aapsLogger, rh, preferences, activePlugin, notificationManager, loop, profileFunction)
    }

    private fun check(timestamp: String, expected: Boolean) {
        val now = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"))
        now.timeInMillis = Instant.parse(timestamp).toEpochMilli()
        assertWithMessage(timestamp).that(plugin.wasDST(now)).isEqualTo(expected)
    }

    @Test fun `spring suspension ends at sixty elapsed minutes`() {
        check("2026-03-29T00:59:59Z", false)
        check("2026-03-29T01:59:00Z", true)
        check("2026-03-29T01:59:59Z", true)
        check("2026-03-29T02:00:00Z", false)
        check("2026-03-29T02:01:00Z", false)
    }

    @Test fun `autumn suspension ends at sixty elapsed minutes across the repeated hour`() {
        check("2026-10-25T00:59:59Z", false)
        check("2026-10-25T01:59:00Z", true)
        check("2026-10-25T01:59:59Z", true)
        check("2026-10-25T02:00:00Z", false)
        check("2026-10-25T02:01:00Z", false)
    }
}
