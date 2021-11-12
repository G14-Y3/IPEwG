package processing.frequency

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import processing.frequency.FreqProcessRange.HighPass
import processing.frequency.FreqProcessRange.LowPass
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

@Serializable
@SerialName("IdleFreqFilter")
class IdleFreqFilter(
    private val boundary: Double = 0.1,
    private val range: FreqProcessRange = HighPass
) : FrequencyFilters() {

    override fun getFilterMatrix(height: Int, width: Int): Array<Array<Double>> {
        val filter = Array(height) { Array(width) { 0.0 } }
        for (x in 0 until height) {
            for (y in 0 until width) {
                val xDist = abs(x - height / 2).toDouble()
                val yDist = abs(y - width / 2).toDouble()
                val distFromCenter = sqrt(xDist.pow(2) + yDist.pow(2))
                if ((distFromCenter < width * boundary && range == HighPass)
                    || (distFromCenter > width * boundary && range == LowPass)
                ) {
                    filter[x][y] = 1.0
                }
            }
        }
        return filter
    }

    override fun toString(): String {
        return "$range idle frequency with boundary $boundary"
    }
}