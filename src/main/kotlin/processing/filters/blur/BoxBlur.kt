package processing.filters.blur

import javafx.scene.image.WritableImage
import processing.ImageProcessing
import processing.filters.Convolution
import processing.filters.SpatialSeparableConvolution

class BoxBlur(private val radius: Int) : ImageProcessing {
    override fun process(image: WritableImage) {
        if (radius == 0) {
            return
        }
        val kernelSize = radius * 2 + 1

        SpatialSeparableConvolution(
            Array(kernelSize) { 1.0 / (kernelSize * kernelSize) },
            Array(kernelSize) { 1.0 }
        ).process(image)
    }

    override fun toString(): String = "BoxBlur with radius $radius"
}