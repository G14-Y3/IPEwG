package processing.filters

import javafx.scene.image.WritableImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import processing.ImageProcessing

@Serializable
@SerialName("Sharpen")
class Sharpen : ImageProcessing {
    override fun process(srcImage: WritableImage, destImage: WritableImage) {
        val kernel = arrayOf(
            arrayOf(-1.0, -1.0, -1.0),
            arrayOf(-1.0, 9.0, -1.0),
            arrayOf(-1.0, -1.0, -1.0)
        )
        Convolution(kernel).process(srcImage, destImage)
    }

    override fun toString(): String = "Sharpen"
}