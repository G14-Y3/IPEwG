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
        val arr = mutableListOf<Color>()
        for (x in 0 until encodeImage.width.toInt()) {
            for (y in 0 until encodeImage.height.toInt()) {
                arr.add(encodeReader.getColor(x, y))
            }
        }

        var index = 0
        for (x in 0 until image.width.toInt()) {
            for (y in 0 until image.height.toInt()) {
                var color: Color = reader.getColor(x, y)
                var encodeColor: Color = color

                if (isByPixelOrder) {
                    encodeColor = arr[index++]
                } else {
                    if (x in 0..encodeImage.width.toInt() && y in 0..encodeImage.height.toInt()) {
                        encodeColor = encodeReader.getColor(x, y)
                    }
                }

                val r = transformBits(color.red, encodeColor.red)
                val g = transformBits(color.green, encodeColor.green)
                val b = transformBits(color.blue, encodeColor.blue)
                color = Color.color(r, g, b)

                writer.setColor(x, y, color)
            }
        }

        // TODO: this encoding technique is temporary, better come up with sth smarter than this
        /* encode metadata for steganography into the first pixel: bits and isByPixelOrder */
        val first_pixel = reader.getArgb(0, 0)
        val isByPixelOrder_bit = if (isByPixelOrder) 1 else 0
        val color = first_pixel and (0b11111111000000001111111111111111.toInt() or (((isByPixelOrder_bit shl 2) or (bits - 1)) shl 16))
        writer.setArgb(0, 0, color)

        /* encode width and height in the second/third pixel */
        val TOP_BITS = 0b11111111000000000000000000000000
        val second_pixel = reader.getArgb(0, 1)
        writer.setArgb(0, 1, encodeImage.width.toInt() or (second_pixel and TOP_BITS.toInt()))
        val third_pixel = reader.getArgb(1, 0)
        writer.setArgb(1, 0, encodeImage.height.toInt() or (third_pixel and TOP_BITS.toInt()))
    }
}