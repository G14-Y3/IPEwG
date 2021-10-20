package processing.filters

import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.RGBType
import processing.ImageProcessing

class RGBIntensity(private val factor: Double, private val type: RGBType) :
    ImageProcessing {
    override fun process(image: WritableImage) {
        val reader: PixelReader = image.pixelReader
        val writer: PixelWriter = image.pixelWriter

        for (x in 0 until image.width.toInt()) {
            for (y in 0 until image.height.toInt()) {
                val color: Color = reader.getColor(x, y)
                var red = color.red
                var green = color.green
                var blue = color.blue
                when (type) {
                    RGBType.R -> red *= factor
                    RGBType.G -> green *= factor
                    else -> blue *= factor
                }
                val newColor: Color = Color.color(red, green, blue)
                writer.setColor(x, y, newColor)
            }
        }
    }

    override fun toString(): String = "${type} Intensity ${(factor * 100).toInt()}%"
}