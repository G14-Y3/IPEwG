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
        val bits = (r and 0b11) + 1
        val isByPixelOrder = (r and 0b100) shr 2

        println(isByPixelOrder)

        /* read width and height from second/third pixel */
        val ENCODE_BITS = 0b111111111111111111111111
        val width = reader.getArgb(0, 1) and ENCODE_BITS
        val height = reader.getArgb(1, 0) and ENCODE_BITS

        val pixel_count = width * height
        val arr = mutableListOf<Color>()
        var count = 0
        loop@ for (x in 0 until image.width.toInt()) {
            for (y in 0 until image.height.toInt()) {
                if (count >= pixel_count) break@loop
                arr.add(reader.getColor(x, y))
                count++
            }
        }

        val pixel_arr = mutableListOf<List<Color>>()
        var index = 0
        loop2@ for (x in 0 until width) {
            val column = mutableListOf<Color>()
            for (y in 0 until height) {
                if (index >= arr.size) break@loop2
                column.add(arr[index++])
            }
            pixel_arr.add(column)
        }

        println(pixel_arr.size)
        println(pixel_arr[0].size)

        for (x in 0 until minOf(width, image.width.toInt(), pixel_arr.size)) {
            for (y in 0 until minOf(height, image.height.toInt(), pixel_arr[0].size)) {
                var decode = reader.getColor(x, y)

                if (isByPixelOrder == 1) decode = pixel_arr[x][y]

                val r = ((decode.red * 255).toInt() shl (8 - bits) and 0b11110000) / 255.0
                val g = ((decode.green * 255).toInt() shl (8 - bits) and 0b11110000) / 255.0
                val b = ((decode.blue * 255).toInt() shl (8 - bits) and 0b11110000) / 255.0
                var color: Color = Color.color(r, g, b)

                writer.setColor(x, y, color)
            }
        }
    }
}