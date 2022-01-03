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
                // for gray scale value, R = G = B, so we can choose any of them to represent the gray value
                val grayvalue = reader.getColor(x, y).grayscale().red
                writer.setColor(x, y, if (grayvalue < threshold) Color.BLACK else Color.WHITE)
            }
        }
    }
}