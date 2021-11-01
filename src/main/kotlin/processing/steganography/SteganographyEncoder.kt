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

    private fun transformBits(origin: Int, encode: Int): Int {
        return (origin and (ALL_ONE shl bits)) or (encode shr (COLOR_BITS_COUNT - bits))
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
                    val r = transformBits(color.red.toInt(), encodeColor.red.toInt())
                    val g = transformBits(color.green.toInt(), encodeColor.green.toInt())
                    val b = transformBits(color.blue.toInt(), encodeColor.blue.toInt())
                    color = Color.color(r.toDouble(), g.toDouble(), b.toDouble())
                }

                writer.setColor(x, y, color)
            }
        }
    }
}