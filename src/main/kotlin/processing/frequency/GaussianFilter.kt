package processing.frequency

import processing.FreqProcessRange

class GaussianFilter(
    private val range: FreqProcessRange,
    private val lowerBoundary: Double,
    private val upperBoundary: Double): FrequencyFilters() {

    override fun getFilterMatrix(height: Int, width: Int): Array<Array<Double>> {
        TODO("Not yet implemented")
    }
}