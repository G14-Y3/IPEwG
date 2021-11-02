package processing.filters


import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.ImageProcessing

class SpatialSeparableConvolution(
    private val kernelCol: Array<Double>,
    private val kernelRow: Array<Double>
) : ImageProcessing {

    init {
        if (kernelCol.size % 2 == 0 || kernelCol.size != kernelRow.size) {
            throw IllegalArgumentException("ill-formed kernel")
        }
    }

    override fun process(image: WritableImage) {
        alterImage(image, kernelCol)
        alterImage(image, kernelRow)
    }

    private fun alterImage(
        image: WritableImage,
        vector: Array<Double>,
    ) {
        val deviation = vector.size / 2
        val width = image.width.toInt()
        val height = image.height.toInt()
        val reader = image.pixelReader
        val writer = image.pixelWriter
        val original = WritableImage(
            reader,
            width,
            height
        )

        for (x in 0 until width) {
            for (y in 0 until height) {
                var sumR = 0.0
                var sumG = 0.0
                var sumB = 0.0
                for (i in -deviation..deviation) {
                    val currX = x + i
                    val currY = y + i
                    val factor = vector[i + deviation]
                    if (currY in 0 until height && currX in 0 until width) {
                        sumR += factor * original.pixelReader.getColor(
                            currX,
                            currY
                        ).red
                        sumG += factor * original.pixelReader.getColor(
                            currX,
                            currY
                        ).green
                        sumB += factor * original.pixelReader.getColor(
                            currX,
                            currY
                        ).blue
                    }

                }
                val newColor = Color(
                    sumR.coerceIn(0.0, 1.0),
                    sumG.coerceIn(0.0, 1.0),
                    sumB.coerceIn(0.0, 1.0),
                    1.0
                )
                writer.setColor(x, y, newColor)
            }
        }
    }

}