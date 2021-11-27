package processing.steganography

import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.ImageProcessing
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt

class SteganographyDecoder(private val isDecodeImage: Boolean): ImageProcessing {

    private var result_image: WritableImage? = null
    private var result_text: String = ""

    override fun process(image: WritableImage) {
        if (isDecodeImage) {
            decodeImage(image)
        } else {
            decodeText(image)
        }
    }

    private fun decodeText(image: WritableImage) {
        val reader: PixelReader = image.pixelReader
        var str = ""
        var length = reader.getArgb(image.width.toInt() - 1, image.height.toInt() - 1) and 0b111111111111111111111111
        loop@for (x in 0 until image.width.toInt()) {
            for (y in 0 until image.height.toInt()) {
                if (length <= 0) break@loop
                val color = reader.getColor(x, y)
                val ch1 = ((((color.green * 255).toInt() and 0b1111) shl 4) or ((color.red * 255).toInt() and 0b1111))
                var res = "${ch1.toChar()}"
                length--
                if (length > 0) {
                    val ch2 = ((((color.opacity * 255).toInt() and 0b1111) shl 4) or ((color.blue * 255).toInt() and 0b1111))
                    length--
                    res = res.plus(ch2.toChar())
                }
                str = str.plus(res)
            }
        }

        println(str)

        result_text = str
    }

    private fun decodeImage(image: WritableImage) {
        val reader: PixelReader = image.pixelReader

        /* read metadata for encoding from the first pixel */
        val first_pixel = reader.getArgb(0, 0)
        val bits = (first_pixel and 0b11) + 1
        val isByPixelOrder = ((first_pixel shr 16) and 0b1) == 1

        /* read width and height from second/third pixel */
        val ENCODE_BITS = 0b111111111111111111111111
        val width = reader.getArgb(0, 1) and ENCODE_BITS
        val height = reader.getArgb(1, 0) and ENCODE_BITS

        result_image = WritableImage(width, height)
        val writer: PixelWriter = result_image!!.pixelWriter

        val pixel_arr = mutableListOf<List<Color>>()

        if (isByPixelOrder) {
            val pixel_arr_temp = mutableListOf<Color>()
            var count = 0
            loop@for (x in 0 until image.width.toInt()) {
                for (y in 0 until image.height.toInt()) {
                    pixel_arr_temp.add(reader.getColor(x, y))
                    if (count++ >= width * height) break@loop
                }
            }
            count = 0
            for (x in 0 until width) {
                val column = mutableListOf<Color>()
                for (y in 0 until height) {
                    column.add(pixel_arr_temp[count++])
                }
                pixel_arr.add(column)
            }
        }

        for (x in 0 until if (isByPixelOrder) width else min(width, image.width.toInt())) {
            for (y in 0 until if (isByPixelOrder) height else min(height, image.height.toInt())) {
                val decode = if (!isByPixelOrder) reader.getColor(x, y) else pixel_arr[x][y]

                val r = ((decode.red * 255).toInt() shl (8 - bits) and 0b11110000) / 255.0
                val g = ((decode.green * 255).toInt() shl (8 - bits) and 0b11110000) / 255.0
                val b = ((decode.blue * 255).toInt() shl (8 - bits) and 0b11110000) / 255.0
                val color: Color = Color.color(r, g, b)

                writer.setColor(x, y, color)
            }
        }
    }

    fun get_result_image(): WritableImage {
        return result_image!!
    }

    fun get_result_text(): String {
        return result_text
    }

    override fun toString(): String {
        return "Decoded target " + if (isDecodeImage) "image" else "text"
    }
}