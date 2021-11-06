package processing.frequency

import processing.FreqProcessRange
import processing.FreqProcessRange.*
import kotlin.math.exp
import kotlin.math.pow

class ButterworthFilter(
    private val range: FreqProcessRange,
    private val passStopBound: Double,
    private val bandWidth: Double,
    private val order: Int)
    : FrequencyFilters() {

    override fun getFilterPixel(dist: Double): Double {
        val baseVal = when (range) {
            LowPass, HighPass ->
                1 / (1 + (passStopBound / dist).pow(2 * order))
            BandPass, BandReject ->
                1 / (1 + (dist * bandWidth / dist.pow(2) - passStopBound.pow(2)).pow(2*order))
        }

        if (range == HighPass || range == BandReject) {
            return 1 - baseVal
        }
        return baseVal
    }

    override fun toString(): String {
        val baseString = "$range order $order butterworth filter, cutoff frequency: ${"%.2f".format(passStopBound)}"
        if (range == BandReject || range == BandPass) {
            return baseString + ", bandwidth: ${"%.2f".format(bandWidth)}"
        }
        return baseString
    }
}