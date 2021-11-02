package processing.filters.blur

import javafx.scene.image.WritableImage
import processing.ImageProcessing
import processing.filters.SpatialSeparableConvolution

class MotionBlur(private val radius: Int, private val angle: Double) :
    ImageProcessing {
    override fun process(image: WritableImage) {
        if (radius == 0) {
            return
        }
        val kernelSize = radius * 2 + 1
        val column = Array(kernelSize) { 0.0 }
        column[radius] = 1.0 / kernelSize

        SpatialSeparableConvolution(
            column, Array(kernelSize) { 1.0 }
        ).process(image)
    }

    override fun toString(): String =
        "Motion Blur with radius $radius and angle $angle"
}