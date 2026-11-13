package app.aaps.plugins.aps.openAPSBoostV5

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class ThoAutoConfigWindowTest {
    private fun profile(days: Int = 2, bg: Int = 700, samples: Int = 8) = BoostV5AutoConfig.V1Profile(
        daysWithData = days, bgReadingCount = bg, tddMedianU = 40.0,
        manualBolusesU = List(samples) { 4.0 }, smbAmountsU = List(100) { 0.3 },
        tbrBelow70Pct = 2.0, timeBelow54Pct = 0.1, meanGlucoseMgdl = 130.0,
        currentMaxIobU = 6.0, currentMaxBolusU = 5.0)

    @Test fun `two valid days and seven hundred readings can derive within a four day window`() {
        val suggestion = BoostV5AutoConfig.compute(profile())
        assertThat(suggestion).isNotNull()
        assertThat(suggestion!!.confirmedCapU).isEqualTo(4.0)
        assertThat(suggestion.committedCapU).isEqualTo(1.0)
    }

    @Test fun `missing minimum history leaves settings underived`() {
        assertThat(BoostV5AutoConfig.compute(profile(days = 1))).isNull()
        assertThat(BoostV5AutoConfig.compute(profile(bg = 699))).isNull()
    }

    @Test fun `four day history is sufficient for both derivation and gap detection`() {
        assertThat(BoostV5AutoConfig.compute(profile(days = 4, bg = 1000))).isNotNull()
        assertThat(BoostHistorySync.isSufficient(BoostHistorySync.History(4, 1000, 100))).isTrue()
    }

    @Test fun `manual bolus evidence begins at eight samples and seven uses SMB fallback`() {
        assertThat(BoostV5AutoConfig.compute(profile(days = 14, bg = 3500, samples = 7))!!.confirmedCapU).isEqualTo(1.5)
        assertThat(BoostV5AutoConfig.compute(profile(days = 14, bg = 3500, samples = 8))!!.confirmedCapU).isEqualTo(4.0)
        assertThat(BoostV5AutoConfig.compute(profile(days = 14, bg = 3500, samples = 9))!!.confirmedCapU).isEqualTo(4.0)
    }

    @Test fun `redrive is due at twenty four hours and not earlier or after a backward clock jump`() {
        val last = 1_000_000_000L
        assertThat(BoostV5AutoConfig.redriveDue(last, last + 86_400_000L)).isTrue()
        assertThat(BoostV5AutoConfig.redriveDue(last, last + 86_399_999L)).isFalse()
        assertThat(BoostV5AutoConfig.redriveDue(last, last - 1L)).isFalse()
        assertThat(BoostV5AutoConfig.redriveDue(0L, last)).isTrue()
    }

    @Test fun `short window with substantial lows still derives caution instead of ignoring them`() {
        val s = BoostV5AutoConfig.compute(profile().copy(tbrBelow70Pct = 8.0, timeBelow54Pct = 2.5))!!
        assertThat(s.hypoCaution).isEqualTo(2.0)
        assertThat(s.aggression).isEqualTo(0.85)
        assertThat(s.aggressiveEarlyConfirm).isFalse()
    }
}
