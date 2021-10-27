package processing.frequency

import processing.FreqProcessRange.*
import processing.FreqProcessRange
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.math.pow

class idleFreqFilter(
    private val boundary: Double = 0.1,
    private val range: FreqProcessRange = HighPass)
    : FrequencyFilters() {

    override fun getFilterMatrix(height: Int, width: Int): Array<Array<Double>> {
        val filter = Array(height) {Array(width) {0.0} }
        for (x in 0 until  height) {
            for (y in 0 until  width) {
                val xDist = abs(x - height / 2).toDouble()
                val yDist = abs(y - width / 2).toDouble()
                val distFromCenter = sqrt(xDist.pow(2) + yDist.pow(2))
                if ((distFromCenter < width * boundary && range == HighPass)
                    || (distFromCenter > width * boundary && range == LowPass)) {
                    filter[x][y] = 1.0
                }
            }
        }
        return filter
    }
}