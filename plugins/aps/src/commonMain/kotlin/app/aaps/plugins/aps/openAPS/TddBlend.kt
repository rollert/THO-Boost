package app.aaps.plugins.aps.openAPS

/** Shared production calculation for the Boost and standalone SMB sensitivity paths. */
internal object TddBlend {
    fun calculate(weighted8H: Double, sevenDay: Double, oneDay: Double, boostPullDown: Boolean = false): Double =
        if (boostPullDown && weighted8H < 0.75 * sevenDay) {
            val adjusted7D = weighted8H + (weighted8H / sevenDay) * (sevenDay - weighted8H)
            adjusted7D * 0.34 + oneDay * 0.33 + weighted8H * 0.33
        } else weighted8H * 0.33 + sevenDay * 0.34 + oneDay * 0.33
}
