package processing.filters

import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.ImageProcessing

class BlackAndWhite(val threshold: Double): ImageProcessing {
    override fun process(srcImage: WritableImage, destImage: WritableImage) {
        val reader = srcImage.pixelReader
        val writer = destImage.pixelWriter

        for (x in 0 until srcImage.width.toInt()) {
            for (y in 0 until srcImage.height.toInt()) {
                val r = reader.getColor(x, y).red
                val g = reader.getColor(x, y).green
                val b = reader.getColor(x, y).blue
                val gray = ((r + g + b) / 3.0 * 255.0)
                writer.setColor(x, y, if (gray < threshold) Color.BLACK else Color.WHITE)
            }
        }
    }
}