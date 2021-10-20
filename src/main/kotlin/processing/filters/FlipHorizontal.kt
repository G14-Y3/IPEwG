package processing.filters

import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import processing.ImageProcessing

class FlipHorizontal : ImageProcessing {
    override fun process(image: WritableImage) {
        val reader: PixelReader = image.pixelReader
        val writer: PixelWriter = image.pixelWriter

        for (x in 0 until image.width.toInt() / 2) {
            for (y in 0 until image.height.toInt()) {
                val colour = reader.getColor(image.width.toInt() - x - 1, y)
                writer.setColor(
                    image.width.toInt() - x - 1,
                    y,
                    reader.getColor(x, y)
                )
                writer.setColor(x, y, colour)
            }
        }
    }

    override fun toString(): String = "Flip Horizontal"
}