package processing.steganography

import javafx.scene.image.Image
import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.ImageProcessing
import kotlin.math.min

class SteganographyDecoder(private var result_image: WritableImage? = null): ImageProcessing {
    override fun process(image: WritableImage) {
        val reader: PixelReader = image.pixelReader

        /* read metadata for encoding from the first pixel */
        val first_pixel = reader.getArgb(0, 0)
        val r = (first_pixel shr 16) and 0b11111111
        val bits = (r and 0b11) + 1
        val isByPixelOrder = ((r and 0b100) shr 2) == 1

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

        for (x in 0 until width) {
            for (y in 0 until height) {
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
}