package processing.resample

sealed interface Interpolation {
    // pass-in the coordinate in the target image, 0-indexed.
    fun getPixel(x: Int, y: Int): RGBA

    override fun toString(): String
}