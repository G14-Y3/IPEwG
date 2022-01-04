package processing.filters

import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import processing.ImageProcessing

@Serializable
@SerialName("Contrast")
class Contrast(private val factor: Double) : ImageProcessing {
    override fun process(srcImage: WritableImage, destImage: WritableImage) {
        val width = srcImage.width.toInt()
        val height = srcImage.height.toInt()
        val reader: PixelReader = srcImage.pixelReader
        val writer: PixelWriter = destImage.pixelWriter
        for (y in 0 until height) {
            for (x in 0 until width) {
                val oldColor = reader.getColor(x, y)
                val newR = (factor * (oldColor.red - 0.5) + 0.5).coerceIn(0.0, 1.0)
                val newG = (factor * (oldColor.green - 0.5) + 0.5).coerceIn(0.0, 1.0)
                val newB = (factor * (oldColor.blue - 0.5) + 0.5).coerceIn(0.0, 1.0)
                val newColor = Color(newR, newG, newB, oldColor.opacity)
                writer.setColor(x, y, newColor)
            }
        }
    }

    override fun toString(): String = "Contrast with ratio $factor"
}