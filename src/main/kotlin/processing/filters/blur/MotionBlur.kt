package processing.filters.blur

import javafx.scene.image.WritableImage
import processing.ImageProcessing
import processing.filters.Convolution
import processing.filters.SpatialSeparableConvolution

class MotionBlur(private val radius: Int, private val angle: Double) :
    ImageProcessing {
    override fun process(image: WritableImage) {
        if (radius == 0) {
            return
        }
        val kernelSize = radius * 2 + 1
        val kernel = Array(kernelSize) { Array(kernelSize) { 0.0 } }

        when (angle) {
            0.0 -> {
                val column = Array(kernelSize) { 0.0 }
                column[radius] = 1.0
                SpatialSeparableConvolution(
                    column, Array(kernelSize) { 1.0 / kernelSize }
                ).process(image)
            }
            45.0 -> {
                for (i in 0 until kernelSize) {
                    kernel[i][kernelSize - i - 1] = 1.0 / kernelSize
                }
                Convolution(kernel).process(image)
            }
            90.0 -> {
                val row = Array(kernelSize) { 0.0 }
                row[radius] = 1.0
                SpatialSeparableConvolution(
                    Array(kernelSize) { 1.0 / kernelSize }, row
                ).process(image)
            }
            135.0 -> {
                for (i in 0 until kernelSize) {
                    kernel[i][i] = 1.0 / kernelSize
                }
                Convolution(kernel).process(image)
            }
            else -> {
                throw IllegalArgumentException("angle has to be 0, 45, 90 or 135")
            }
        }
    }

    override fun toString(): String =
        "Motion Blur with radius $radius and angle $angle"
}