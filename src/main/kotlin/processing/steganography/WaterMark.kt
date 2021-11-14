package processing.steganography

import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.ImageProcessing

class WaterMark(val encodeImage: Image, verticalGap: Int, horizontalGap: Int): ImageProcessing {

    val COLOR_BITS_COUNT = 8
    val ALL_ONE = 0b11111111
    val LSB_BITS = 4

    private fun transformBits(origin: Double, encode: Double): Double {
        val origin_int = (origin * 255).toInt()
        val encode_int = (encode * 255).toInt()
        val result = (origin_int and (ALL_ONE shl LSB_BITS)) or (encode_int shr (COLOR_BITS_COUNT - LSB_BITS))
        return result / 255.0
    }

    // Use the LSB technique that is used in Steganography
    override fun process(image: WritableImage) {
        val reader = image.pixelReader
        val encodeReader = encodeImage.pixelReader
        val writer = image.pixelWriter
        val encodeWidth = encodeImage.width.toInt()
        val encodeHeight = encodeImage.height.toInt()

        for (x in 0 until image.width.toInt()) {
            for (y in 0 until image.height.toInt()) {
                var color: Color = reader.getColor(x, y)
                var encodeColor: Color = encodeReader.getColor(x % encodeWidth, y % encodeHeight)

                val r = transformBits(color.red, encodeColor.red)
                val g = transformBits(color.green, encodeColor.green)
                val b = transformBits(color.blue, encodeColor.blue)
                color = Color.color(r, g, b)

                writer.setColor(x, y, color)
            }
        }
    }
}