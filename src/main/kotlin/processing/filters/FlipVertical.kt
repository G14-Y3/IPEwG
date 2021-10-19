package processing.filters

import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import processing.ImageProcessing

class FlipVertical : ImageProcessing {
    override fun process(image: WritableImage) {
        val reader: PixelReader = image.pixelReader
        val writer: PixelWriter = image.pixelWriter

        for (x in 0 until image.width.toInt()) {
            for (y in 0 until image.height.toInt() / 2) {
                val colour = reader.getColor(x, image.height.toInt() - y - 1)
                writer.setColor(
                    x,
                    image.height.toInt() - y - 1,
                    reader.getColor(x, y)
                )
                writer.setColor(x, y, colour)
            }
        }
    }
}