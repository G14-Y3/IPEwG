package processing.frequency

interface FilterGenerator {

    fun getFilterPixel(dist: Double): Double
}