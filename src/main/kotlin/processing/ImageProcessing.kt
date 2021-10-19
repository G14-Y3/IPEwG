package processing

import javafx.scene.image.WritableImage

enum class RGBType { R, G, B }

enum class HSVType { H, S, V }

interface ImageProcessing {

    fun process(image: WritableImage)
}
