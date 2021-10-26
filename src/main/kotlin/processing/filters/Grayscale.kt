package processing.filters

import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
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

fun writeGrayImage(input: Array<DoubleArray>, image: WritableImage) {
    for (x in 0 until image.width.toInt()) {
        for (y in 0 until image.height.toInt()) {
            image.pixelWriter.setColor(x, y, Color.color(input[x][y], input[x][y], input[x][y]))
        }
    }
}