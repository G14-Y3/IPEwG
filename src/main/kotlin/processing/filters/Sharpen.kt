package processing.filters

import javafx.scene.image.WritableImage
import processing.ImageProcessing

class Sharpen : ImageProcessing {
    override fun process(image: WritableImage) {
        val kernel = arrayOf(
            arrayOf(-1.0, -1.0, -1.0),
            arrayOf(-1.0, 9.0, -1.0),
            arrayOf(-1.0, -1.0, -1.0)
        )
        Convolution(kernel).process(image)
    }
}