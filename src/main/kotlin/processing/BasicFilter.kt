package processing

import javafx.scene.image.Image
import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color

class BasicFilter : ImageProcessing {
    companion object {
        // TODO: reduce code duplication here
        fun greyscaleFilter(image: WritableImage) {
            val reader: PixelReader = image.pixelReader
            val writer: PixelWriter = image.pixelWriter

            for (x in 0 until image.width.toInt()) {
                for (y in 0 until image.height.toInt()) {
                    writer.setColor(x, y, reader.getColor(x, y).grayscale())
                }
            }
        }

        fun inverseColorFilter(image: WritableImage) {
            val reader: PixelReader = image.pixelReader
            val writer: PixelWriter = image.pixelWriter

            for (x in 0 until image.width.toInt()) {
                for (y in 0 until image.height.toInt()) {
                    writer.setColor(x, y, reader.getColor(x, y).invert())
                }
            }
        }

        fun RGBColorFilter(raw: Image, result: WritableImage, factor: Double) {
            val reader: PixelReader = raw.pixelReader
            val writer: PixelWriter = result.pixelWriter

            for (x in 0 until raw.width.toInt()) {
                for (y in 0 until raw.height.toInt()) {
                    val color : Color = reader.getColor(x, y)
                    val newColor : Color = Color.color(color.red * factor, color.green, color.blue)
                    writer.setColor(x, y, newColor)
                }
            }
        }
    }
}