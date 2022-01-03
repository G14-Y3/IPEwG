package processing.resample

import javafx.scene.image.PixelReader

sealed interface Interpolation {
    // pass-in the coordinate in the target image, 0-indexed.
    fun getPixel(reader: PixelReader, x: Int, y: Int): RGBA

    override fun toString(): String
}

sealed interface Params {
    override fun toString(): String
}