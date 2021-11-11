package processing.filters

import javafx.scene.image.WritableImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import processing.ImageProcessing

@Serializable
@SerialName("Contrast")
class Contrast : ImageProcessing {
    override fun process(image: WritableImage) {
        TODO("Not yet implemented")
    }
}