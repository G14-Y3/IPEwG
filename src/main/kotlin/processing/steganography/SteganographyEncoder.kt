package processing.steganography

import javafx.scene.image.Image
import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.ImageProcessing

class SteganographyEncoder(val encodeImage: Image, val key: String, val bits: Int, val isByPixelOrder: Boolean): ImageProcessing {

    val COLOR_BITS_COUNT = 8
    val ALL_ONE = 0b11111111

    private fun transformBits(origin: Double, encode: Double): Double {
        val origin_int = (origin * 255).toInt()
        val encode_int = (encode * 255).toInt()
        val result = (origin_int and (ALL_ONE shl bits)) or (encode_int shr (COLOR_BITS_COUNT - bits))
        return result / 255.0
    }

    override fun process(image: WritableImage) {
        val reader: PixelReader = image.pixelReader
        val writer: PixelWriter = image.pixelWriter

        val encodeReader: PixelReader = encodeImage.pixelReader

        // TODO: reduce the dimensionality of encode image if it is larger than original image
        for (x in 0 until image.width.toInt()) {
            for (y in 0 until image.height.toInt()) {
                var color: Color = reader.getColor(x, y)

                if (x in 0..encodeImage.width.toInt() && y in 0..encodeImage.height.toInt()) {
                    val encodeColor: Color = encodeReader.getColor(x, y)
                    val r = transformBits(color.red, encodeColor.red)
                    val g = transformBits(color.green, encodeColor.green)
                    val b = transformBits(color.blue, encodeColor.blue)
                    color = Color.color(r, g, b)
                }

                writer.setColor(x, y, color)
            }
        }
    }
}