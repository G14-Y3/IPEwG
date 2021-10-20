package processing.filters

import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import processing.ImageProcessing

class Grayscale : ImageProcessing {
    override fun process(image: WritableImage) {
        val reader: PixelReader = image.pixelReader
        val writer: PixelWriter = image.pixelWriter

        for (x in 0 until image.width.toInt()) {
            for (y in 0 until image.height.toInt()) {
                writer.setColor(x, y, reader.getColor(x, y).grayscale())
            }
        }
    }

    override fun toString(): String = "Grayscale"

}