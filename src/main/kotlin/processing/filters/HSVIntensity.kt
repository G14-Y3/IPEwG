package processing.filters

import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.HSVType
import processing.ImageProcessing

class HSVIntensity(private val factor: Double, private val type: HSVType) :
    ImageProcessing {
    override fun process(image: WritableImage) {
        val reader: PixelReader = image.pixelReader
        val writer: PixelWriter = image.pixelWriter

        for (x in 0 until image.width.toInt()) {
            for (y in 0 until image.height.toInt()) {
                val color: Color = reader.getColor(x, y)
                val newColor: Color = when (type) {
                    HSVType.H -> color.deriveColor((factor - 1) * 180, 1.0, 1.0, 1.0)
                    HSVType.S -> color.deriveColor(0.0, factor, 1.0, 1.0)
                    HSVType.V -> color.deriveColor(0.0, 1.0, factor, 1.0)
                }
                writer.setColor(x, y, newColor)
            }
        }
    }

    override fun toString(): String = "${type}=${(factor * 100 - 100).toInt()}%"
}
