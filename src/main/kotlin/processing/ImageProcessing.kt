package processing

import javafx.scene.image.WritableImage

enum class RGBType { R, G, B }

interface ImageProcessing {

    fun process(image: WritableImage)
}
