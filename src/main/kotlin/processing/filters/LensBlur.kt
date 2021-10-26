package processing.filters

import javafx.scene.image.WritableImage
import processing.ImageProcessing
import kotlin.math.pow

class LensBlur(private val radius: Int) : ImageProcessing {
    override fun process(image: WritableImage) {
        if (radius == 0) {
            return
        }
        val kernelSize = radius * 2 + 1
        val kernel = Array(kernelSize) { Array(kernelSize) { 0.0 } }
        val center = radius
        var total = 0
        for (i in 0 until kernelSize) {
            for (j in 0 until kernelSize) {
                val distance =
                    ((i - center).toDouble().pow(2) + (j - center).toDouble().pow(2)).pow(0.5)
                if (distance <= radius) {
                    kernel[i][j] = 1.0
                    total++
                }
            }
        }
        for (i in 0 until kernelSize) {
            for (j in 0 until kernelSize) {
                kernel[i][j] = kernel[i][j] / total
            }
        }
        Convolution(kernel).process(image)
    }
}
