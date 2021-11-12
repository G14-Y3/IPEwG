package processing.steganography

import javafx.scene.image.Image
import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.ImageProcessing

class SteganographyEncoder(
    val isEncodeImage: Boolean, val encodeText: String, val onlyRChannel: Boolean, val encodeImage: Image?,
    val key: String, val bits: Int, val isByPixelOrder: Boolean): ImageProcessing {

    constructor(encodeImage: Image, key: String, bits: Int, isByPixelOrder: Boolean)
            : this(true, "", false, encodeImage, key, bits, isByPixelOrder)

    constructor(encodeText: String, onlyRChannel: Boolean, key: String, bits: Int)
            : this(false, encodeText, onlyRChannel, null, key, bits, false)

    val COLOR_BITS_COUNT = 8
    val ALL_ONE = 0b11111111

    private fun transformBits(origin: Double, encode: Double): Double {
        val origin_int = (origin * 255).toInt()
        val encode_int = (encode * 255).toInt()
        val result = (origin_int and (ALL_ONE shl bits)) or (encode_int shr (COLOR_BITS_COUNT - bits))
        return result / 255.0
    }

    private fun transformBits(origin: Double, encode: Int): Double {
        val origin_int = (origin * 255).toInt()
        val encode_int = encode
        val result = (origin_int and (ALL_ONE shl bits)) or encode_int
        return result / 255.0
    }

    override fun process(image: WritableImage) {
        if (isEncodeImage) {
            encodeImage(image)
        } else {
            encodeText(image)
            encodeText(image)
        }
    }

    private fun encodeImage(image: WritableImage) {
        val reader: PixelReader = image.pixelReader
        val writer: PixelWriter = image.pixelWriter

        val encodeReader: PixelReader = encodeImage!!.pixelReader
        val encode_width = encodeImage.width.toInt()
        val encode_height = encodeImage.height.toInt()

        val arr = mutableListOf<Color>()
        for (x in 0 until encode_width) {
            for (y in 0 until encode_height) {
                arr.add(encodeReader.getColor(x, y))
            }
        }

        var index = 0
        for (x in 0 until image.width.toInt()) {
            for (y in 0 until image.height.toInt()) {
                var color: Color = reader.getColor(x, y)
                var encodeColor: Color = color

                if (isByPixelOrder) {
                    if (index < encode_width * encode_height) {
                        encodeColor = arr[index++]
                    }
                } else {
                    if (x in 0 until encode_width && y in 0 until encode_height) {
                        encodeColor = encodeReader.getColor(x, y)
                    }
                }

                if ((isByPixelOrder && index < encode_width * encode_height) ||
                    (!isByPixelOrder && x in 0 until encode_width && y in 0 until encode_height)) {
                    val r = transformBits(color.red, encodeColor.red)
                    val g = transformBits(color.green, encodeColor.green)
                    val b = transformBits(color.blue, encodeColor.blue)
                    color = Color.color(r, g, b)
                }

                writer.setColor(x, y, color)
            }
        }

        // TODO: this encoding technique is temporary, better come up with sth smarter than this
        /* encode metadata for steganography into the first pixel: bits and isByPixelOrder */
        val first_pixel = reader.getArgb(0, 0)
        val isByPixelOrder_bit = if (isByPixelOrder) 1 else 0
        val color = 0b11111111000000001111111100000000.toInt() or (((bits - 1) or (isByPixelOrder_bit shl 16)))
        writer.setArgb(0, 0, color)

        /* encode width and height in the second/third pixel */
        val TOP_BITS = 0b11111111000000000000000000000000
        val second_pixel = reader.getArgb(0, 1)
        writer.setArgb(0, 1, encode_width or (second_pixel and TOP_BITS.toInt()))
        val third_pixel = reader.getArgb(1, 0)
        writer.setArgb(1, 0, encode_height or (third_pixel and TOP_BITS.toInt()))
    }

    private fun encodeText(image: WritableImage) {
        /* encode text into the RGBA channels of the image */
        val reader: PixelReader = image.pixelReader
        val writer: PixelWriter = image.pixelWriter

        var count = 0
        loop@for (x in 0 until image.width.toInt()) {
            for (y in 0 until image.height.toInt()) {
                if (count >= encodeText.length) break@loop
                var color = reader.getColor(x, y)
                val ch1 = encodeText[count++]
                val r = transformBits(color.red, ch1.code and 0b1111)
                val g = transformBits(color.green, (ch1.code shr 4) and 0b1111)
                if (count >= encodeText.length) {
                    color = Color.color(r, g, color.blue, color.opacity)
                    writer.setColor(x, y, color)
                    break@loop
                }
                val ch2 = encodeText[count++]
                val b = transformBits(color.blue, ch2.code and 0b1111)
                val a = transformBits(color.opacity, (ch2.code shr 4) and 0b1111)
                color = Color.color(r, g, b, a)
                writer.setColor(x, y, color)
            }
        }

        writer.setArgb(image.width.toInt() - 1, image.height.toInt() - 1, encodeText.length or 0b11111111000000000000000000000000.toInt())
    }

    override fun toString(): String {
        return "Encoded target " + if (isEncodeImage) "image" else "text"
    }
}