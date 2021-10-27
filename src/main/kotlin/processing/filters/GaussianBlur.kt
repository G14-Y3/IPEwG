package processing.filters

import javafx.scene.image.WritableImage
import processing.ImageProcessing
import kotlin.math.PI
import kotlin.math.exp

class GaussianBlur(private val radius: Int) : ImageProcessing {
    override fun process(image: WritableImage) {
        if (radius == 0) {
            return
        }
        val kernelSize = radius * 2 + 1
        val kernel = Array(kernelSize) { Array(kernelSize) { 0.0 } }
        for (i in 0 until kernelSize) {
            for (j in 0 until kernelSize) {
                val x = j - radius
                val y = i - radius
                val sigma = radius / 2.0
                val coefficient = 1.0 / (2.0 * PI * sigma * sigma)
                kernel[i][j] = coefficient * exp(-(x * x + y * y) / (2.0 * sigma * sigma))
            }
        }
        Convolution(kernel).process(image)
    }

    override fun toString(): String = "Gaussian Blur with radius $radius"
}
