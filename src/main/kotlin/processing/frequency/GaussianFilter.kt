package processing.frequency

import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import kotlin.math.exp
import kotlin.math.pow
import processing.frequency.FreqProcessRange.*

class GaussianFilter(
    override val filterImageView: ImageView,
    private val range: FreqProcessRange,
    private val passStopBound: Double,
    private val bandWidth: Double)
    : FilterGenerator() {

    // Reference: http://faculty.salina.k-state.edu/tim/mVision/freq-domain/freq_filters.html
    override fun getFilterPixel(dist: Double): Double {
        val baseVal = when (range) {
            LowPass, HighPass -> exp(
                -dist.pow(2) / (2.0 * passStopBound.pow(2)))
            BandPass, BandReject -> exp(
                -((dist.pow(2) - passStopBound.pow(2)) / (dist * bandWidth))
                    .pow(2))
        }
        if (range == HighPass || range == BandReject) {
            return 1 - baseVal
        }
        return baseVal
    }

    override fun toString(): String {
        val baseString = "$range gaussian filter, cutoff frequency: ${"%.2f".format(passStopBound)}"
        if (range == BandReject || range == BandPass) {
            return baseString + ", bandwidth: ${"%.2f".format(bandWidth)}"
        }
        return baseString
    }
}