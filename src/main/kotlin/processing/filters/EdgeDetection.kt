package processing.filters

import javafx.scene.image.WritableImage
import processing.ImageProcessing

class EdgeDetection : ImageProcessing {
    override fun process(image: WritableImage) {
        Grayscale().process(image)
        GaussianBlur(5).process(image)
    }

    override fun toString(): String = "Edge detection"
}