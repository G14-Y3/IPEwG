package processing.filters

import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.ImageProcessing

/**
 * @param kernel n*n matrix, with odd n
 */
class Convolution(private val kernel: Array<Array<Double>>) : ImageProcessing {
    init {
        if (kernel.size % 2 == 0 || kernel.any { it.size != kernel.size }) {
            throw IllegalArgumentException("ill-formed kernel")
        }
    }

    override fun process(image: WritableImage) {
        val deviation = kernel.size / 2
        val width = image.width.toInt()
        val height = image.height.toInt()
        val reader: PixelReader = image.pixelReader
        val writer: PixelWriter = image.pixelWriter
        val original = WritableImage(
            reader,
            width,
            height
        )
        for (y in 0 until height) {
            for (x in 0 until width) {
                var sumR = 0.0
                var sumG = 0.0
                var sumB = 0.0
                for (i in -deviation..deviation) {
                    for (j in -deviation..deviation) {
                        val currY = y + i
                        val currX = x + j
                        val factor = kernel[i + deviation][j + deviation]
                        if (currY in 0 until height && currX in 0 until width) {
                            sumR += factor * original.pixelReader.getColor(currX, currY).red
                            sumG += factor * original.pixelReader.getColor(currX, currY).green
                            sumB += factor * original.pixelReader.getColor(currX, currY).blue
                        }
                    }
                }
                val newColor = Color(
                    sumR.coerceAtMost(1.0),
                    sumG.coerceAtMost(1.0),
                    sumB.coerceAtMost(1.0),
                    1.0
                )
                writer.setColor(x, y, newColor)
            }
        }
    }
}