package processing.frequency

import javafx.scene.image.ImageView
import processing.frequency.FreqProcessRange.*
import kotlin.math.pow

class ButterworthFilter(
    override val filterImageView: ImageView,
    private val range: FreqProcessRange,
    private val passStopBound: Double,
    private val bandWidth: Double,
    private val order: Int)
    : FilterGenerator() {

    // Reference: http://faculty.salina.k-state.edu/tim/mVision/freq-domain/freq_filters.html
    override fun getFilterPixel(dist: Double): Double {
        val baseVal = when (range) {
            LowPass, HighPass ->
                1 / (1 + (dist / passStopBound).pow(2 * order))
            BandPass, BandReject ->
                1 / (1 + (dist * bandWidth / (dist.pow(2) - passStopBound.pow(2))).pow(2*order))
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