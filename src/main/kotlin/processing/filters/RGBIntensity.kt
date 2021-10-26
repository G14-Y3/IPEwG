package processing.filters

import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.ImageProcessing
import processing.RGBType

class RGBIntensity(private val factor: Double, private val type: RGBType) :
    ImageProcessing {
    override fun process(image: WritableImage) {
        val reader: PixelReader = image.pixelReader
        val writer: PixelWriter = image.pixelWriter

        for (x in 0 until image.width.toInt()) {
            for (y in 0 until image.height.toInt()) {
                val color: Color = reader.getColor(x, y)
                val red = color.red
                val green = color.green
                val blue = color.blue
                val newColor: Color = when (type) {
                    RGBType.R -> Color.color((red * factor).coerceAtMost(1.0), green, blue)
                    RGBType.G -> Color.color(red, (green * factor).coerceAtMost(1.0), blue)
                    RGBType.B -> Color.color(red, green, (blue * factor).coerceAtMost(1.0))
                }
                writer.setColor(x, y, newColor)
            }
        }
    }

    override fun toString(): String = "${type}=${(factor * 100 - 100).toInt()}%"
}