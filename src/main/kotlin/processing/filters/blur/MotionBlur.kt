package processing.filters.blur

import javafx.scene.image.WritableImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import processing.ImageProcessing
import processing.filters.Convolution
import processing.filters.SpatialSeparableConvolution

@Serializable
@SerialName("MotionBlur")
class MotionBlur(private val radius: Int, private val angle: Double) :
    ImageProcessing {
    override fun process(srcImage: WritableImage, destImage: WritableImage) {
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
                ).process(srcImage, destImage)
            }
            45.0 -> {
                for (i in 0 until kernelSize) {
                    kernel[i][kernelSize - i - 1] = 1.0 / kernelSize
                }
                Convolution(kernel).process(srcImage, destImage)
            }
            90.0 -> {
                val row = Array(kernelSize) { 0.0 }
                row[radius] = 1.0
                SpatialSeparableConvolution(
                    Array(kernelSize) { 1.0 / kernelSize }, row
                ).process(srcImage, destImage)
            }
            135.0 -> {
                for (i in 0 until kernelSize) {
                    kernel[i][i] = 1.0 / kernelSize
                }
                Convolution(kernel).process(srcImage, destImage)
            }
            else -> {
                throw IllegalArgumentException("angle has to be 0, 45, 90 or 135")
            }
        }
    }

    override fun toString(): String =
        "Motion Blur with radius $radius and angle $angle"
}