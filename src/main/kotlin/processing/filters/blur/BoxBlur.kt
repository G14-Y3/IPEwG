package processing.filters.blur

import javafx.scene.image.WritableImage
import processing.ImageProcessing
import processing.filters.Convolution

class BoxBlur(private val radius: Int) : ImageProcessing {
    override fun process(image: WritableImage) {
        if (radius == 0) {
            return
        }
        val kernelSize = radius * 2 + 1
        val kernel = Array(kernelSize) { Array(kernelSize) { 0.0 } }
        val total = kernelSize * kernelSize
        for (i in 0 until kernelSize) {
            for (j in 0 until kernelSize) {
                kernel[i][j] = 1.0 / total
            }
        }
        Convolution(kernel).process(image)
    }

    override fun toString(): String = "BoxBlur with radius $radius"
}