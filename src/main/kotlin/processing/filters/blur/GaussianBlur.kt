package processing.filters.blur

import javafx.scene.image.WritableImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import processing.ImageProcessing
import processing.filters.SpatialSeparableConvolution
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow

@Serializable
@SerialName("GaussianBlur")
class GaussianBlur(private val radius: Int) : ImageProcessing {
    override fun process(image: WritableImage) {
        if (radius == 0) {
            return
        }
        val kernelSize = radius * 2 + 1
        val rowKernel = Array(kernelSize) {
            val x = it - radius
            val sigma = radius / 2.0
            val coefficient = 1.0 / (2.0 * PI * sigma * sigma).pow(0.5)
            coefficient * exp(-(x * x) / (2.0 * sigma * sigma))
        }
        SpatialSeparableConvolution(
            rowKernel,
            rowKernel
        ).process(image)
    }

    override fun toString(): String = "Gaussian Blur with radius $radius"
}

fun generateGaussianKernel(radius: Int): Array<Array<Double>> {
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
    return kernel
}