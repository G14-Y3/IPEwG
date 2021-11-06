package processing.frequency

import processing.FreqProcessRange.*
import processing.FreqProcessRange
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sqrt
import kotlin.math.pow

class IdleFreqFilter(
    private val range: FreqProcessRange,
    private val passStopBound: Double,
    private val bandWidth: Double)
    : FrequencyFilters() {

    override fun getFilterPixel(dist: Double): Double {
        val rangeToBool = when (range) {
            LowPass -> dist <= passStopBound
            HighPass -> dist > passStopBound
            BandPass -> passStopBound - bandWidth / 2.0 <= dist && dist <= passStopBound + bandWidth / 2.0
            BandReject -> dist < passStopBound - bandWidth / 2.0 || passStopBound + bandWidth / 2.0 < dist
        }

        if (rangeToBool) {
            return 1.0
        }
        return 0.0
    }

    override fun toString(): String {
        var baseString = "$range idle filter, cutoff frequency: ${"%.2f".format(passStopBound)}"
        if (range == BandReject || range == BandPass) {
            return baseString + ", bandwidth: ${"%.2f".format(bandWidth)}"
        }
        return baseString
    }
}