package processing.frequency

import processing.FreqProcessRange

class ButterWorthFilter(
    private val range: FreqProcessRange,
    private val lowerBound: Double,
    private val upperBound: Double) : FrequencyFilters() {

    override fun getFilterMatrix(height: Int, width: Int): Array<Array<Double>> {
        TODO("Not yet implemented")
    }

}
