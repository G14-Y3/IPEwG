package processing.frequency

import processing.FreqProcessRange

class GaussianFilter(
    private val range: FreqProcessRange,
    private val centerFreq: Double,
    private val passBound: Double): FrequencyFilters() {

    override fun getFilterMatrix(height: Int, width: Int): Array<Array<Double>> {
        TODO("Not yet implemented")
    }
}