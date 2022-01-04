package processing.resample

sealed interface Interpolation {
    // pass-in the coordinate in the target image, 0-indexed.
    fun getPixel(reader: RGBAReader, x: Int, y: Int): RGBA

    override fun toString(): String
}

sealed interface Params {
    override fun toString(): String
}