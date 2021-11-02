package processing.steganography

import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.ImageProcessing

class SteganographyDecoder: ImageProcessing {

    override fun process(image: WritableImage) {
        val reader: PixelReader = image.pixelReader
        val writer: PixelWriter = image.pixelWriter

        /* read metadata for encoding from the first pixel */
        val first_pixel = reader.getArgb(0, 0)
        val r = (first_pixel shr 16) and 0b11111111
        val bits = 4 //r and 0b11
        val isByPixelOrder = r and 0b100

        /* read width and height from second/third pixel */
        val width = reader.getArgb(0, 1)
        val height = reader.getArgb(1, 0)

        for (x in 0 until width) {
            for (y in 0 until height) {
                var decode = reader.getColor(x, y)
                val r = ((decode.red * 256).toInt() shl (8 - bits))
                val g = ((decode.green * 256).toInt() shl (8 - bits))
                val b = ((decode.blue * 256).toInt() shl (8 - bits))
                var color: Color = Color.color(r.toDouble(), g.toDouble(), b.toDouble())

                writer.setColor(x, y, color)
            }
        }
    }
}