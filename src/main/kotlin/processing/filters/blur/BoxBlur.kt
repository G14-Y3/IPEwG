package processing.filters.blur

import javafx.scene.image.WritableImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import processing.ImageProcessing
import processing.filters.SpatialSeparableConvolution

@Serializable
@SerialName("BoxBlur")
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