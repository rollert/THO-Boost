package app.aaps.plugins.aps.openAPSBoost

import app.aaps.plugins.aps.openAPS.TddBlend
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

/** Exercises the production helper called by both plugins, not a copy of its formula. */
class ThoTddPolicyTest {
    @Test fun `standalone SMB follows recent delivery without Boost pull-down`() {
        assertThat(TddBlend.calculate(8.0, 40.0, 8.0)).isWithin(1e-9).of(11.2)
        assertThat(TddBlend.calculate(5.0, 40.0, 5.0)).isWithin(1e-9).of(8.5)
    }

    @Test fun `Boost preserves a plausible rapid drop with healthy week history`() {
        val tdd = TddBlend.calculate(8.0, 40.0, 8.0, boostPullDown = true)
        assertThat(tdd).isWithin(1e-9).of(11.2)
        assertThat(tddImplausibleForProfile(tdd, profileSens = 45.0, tdd7D = 40.0)).isFalse()
    }

    @Test fun `pull-down starts strictly below fifteen percent of seven day TDD`() {
        assertThat(TddBlend.calculate(6.0, 40.0, 6.0, boostPullDown = true)).isWithin(1e-9).of(9.4)
        assertThat(TddBlend.calculate(5.0, 40.0, 5.0, boostPullDown = true)).isWithin(1e-9).of(5.4375)
    }

    @Test fun `healthy history does not exempt a near zero blended delivery`() {
        assertThat(tddImplausibleForProfile(5.99, profileSens = 45.0, tdd7D = 40.0)).isTrue()
        assertThat(tddImplausibleForProfile(6.0, profileSens = 45.0, tdd7D = 40.0)).isFalse()
    }

    @Test fun `corrupt seven day history is rejected even when recent blend looks healthy`() {
        assertThat(tddImplausibleForProfile(20.0, profileSens = 45.0, tdd7D = 13.99)).isTrue()
        assertThat(tddImplausibleForProfile(20.0, profileSens = 45.0, tdd7D = 14.0)).isFalse()
    }

    @Test fun `non finite delivery cannot produce dynamic sensitivity`() {
        assertThat(tddImplausibleForProfile(Double.NaN, 45.0, 40.0)).isTrue()
        assertThat(tddImplausibleForProfile(Double.POSITIVE_INFINITY, 45.0, 40.0)).isTrue()
        assertThat(tddImplausibleForProfile(20.0, 45.0, Double.NaN)).isTrue()
    }

    @Test fun `unchanged stable delivery remains unchanged by the blend`() {
        assertThat(TddBlend.calculate(40.0, 40.0, 40.0, boostPullDown = true)).isWithin(1e-9).of(40.0)
    }
}
