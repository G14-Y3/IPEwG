package processing

import javafx.scene.image.Image
import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import models.RGB_type

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

        fun RGBColorFilter(result: WritableImage, factor: Double, type: RGB_type) {
            val reader: PixelReader = result.pixelReader
            val writer: PixelWriter = result.pixelWriter

            for (x in 0 until result.width.toInt()) {
                for (y in 0 until result.height.toInt()) {
                    val color : Color = reader.getColor(x, y)
                    var red = color.red
                    var green = color.green
                    var blue = color.blue
                    if (type == RGB_type.R) {
                        red *= factor
                    } else if (type == RGB_type.G) {
                        green *= factor
                    } else {
                        blue *= factor
                    }
                    val newColor : Color = Color.color(red, green, blue)
                    writer.setColor(x, y, newColor)
                }
            }
        }
    }
}