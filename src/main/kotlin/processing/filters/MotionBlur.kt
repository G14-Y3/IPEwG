package processing.filters

import javafx.scene.image.WritableImage
import processing.ImageProcessing

class MotionBlur(private val radius: Int, private val angle: Double) : ImageProcessing {
    override fun process(image: WritableImage) {
        if (radius == 0) {
            return
        }
        val kernelSize = radius * 2 + 1
        val kernel = Array(kernelSize) { Array(kernelSize) { 0.0 } }
        val total = kernelSize
        val center = radius
        when (angle) {
            0.0 -> {
                for (j in 0 until kernelSize) {
                    kernel[center][j] = 1.0 / total
                }
            }
            45.0 -> {
                for (i in 0 until kernelSize) {
                    kernel[i][kernelSize - i - 1] = 1.0 / total
                }
            }
            90.0 -> {
                for (i in 0 until kernelSize) {
                    kernel[i][center] = 1.0 / total
                }
            }
            135.0 -> {
                for (i in 0 until kernelSize) {
                    kernel[i][i] = 1.0 / total
                }
            }
            else -> {
                throw IllegalArgumentException("angle has to be 0, 45, 90 or 135")
            }
        }
        Convolution(kernel).process(image)
    }

    override fun toString(): String = "Motion Blur with radius $radius and angle $angle"
}