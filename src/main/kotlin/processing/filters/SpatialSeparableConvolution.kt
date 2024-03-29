package processing.filters


import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import processing.ImageProcessing

@Serializable
@SerialName("SpatialSeparableConvolution")
class SpatialSeparableConvolution(
    private val kernelCol: Array<Double>,
    private val kernelRow: Array<Double>
) : ImageProcessing {

    init {
        if (kernelCol.size % 2 == 0 || kernelCol.size != kernelRow.size) {
            throw IllegalArgumentException("ill-formed kernel")
        }
    }

    override fun process(srcImage: WritableImage, destImage: WritableImage) {
        val temp = WritableImage(srcImage.width.toInt(), srcImage.height.toInt())
        alterImage(srcImage, temp, kernelCol, transpose = true)
        alterImage(temp, destImage, kernelRow, transpose = false)
    }

    private fun alterImage(
        srcImage: WritableImage,
        destImage: WritableImage,
        vector: Array<Double>,
        transpose: Boolean
    ) {
        val deviation = vector.size / 2
        val width = srcImage.width.toInt()
        val height = srcImage.height.toInt()
        val reader = srcImage.pixelReader
        val writer = destImage.pixelWriter
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
                    val currX = if (!transpose) x + i else x
                    val currY = if (!transpose) y else y + i
                    val factor = vector[i + deviation]
                    if (currY in 0 until height && currX in 0 until width) {
                        sumR += factor * original.pixelReader.getColor(currX, currY).red
                        sumG += factor * original.pixelReader.getColor(currX, currY).green
                        sumB += factor * original.pixelReader.getColor(currX, currY).blue
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