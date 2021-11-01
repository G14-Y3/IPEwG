package processing.filters

import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.ImageProcessing
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

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
        when (val numCores = Runtime.getRuntime().availableProcessors()) {
            1 -> singleThreadedProcess(image)
            else -> multiThreadedProcess(
                image,
                numCores
            )
        }
    }

    private fun singleThreadedProcess(image: WritableImage) {
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

    private fun multiThreadTask(
        kernelIndex: Int,
        image: WritableImage,
        reader: PixelReader
    ) {
        val deviation = kernel.size / 2
        for (x in 0 until image.width.toInt()) {
            for (y in 0 until image.height.toInt()) {
                var sumR = 0.0
                var sumG = 0.0
                var sumB = 0.0

                for (i in -deviation..deviation) {
                    val curY = y + kernelIndex - deviation
                    val curX = x + i
                    val factor = kernel[kernelIndex][i + deviation]
                    if (curY in 0 until image.height.toInt() && curX in 0 until image.width.toInt()) {
                        sumR += factor * reader.getColor(curX, curY).red
                        sumG += factor * reader.getColor(curX, curY).green
                        sumB += factor * reader.getColor(curX, curY).blue
                    }
                }
                image.pixelWriter.setColor(
                    x, y, Color(
                        sumR.coerceIn(0.0, 1.0),
                        sumG.coerceIn(0.0, 1.0),
                        sumB.coerceIn(0.0, 1.0),
                        1.0
                    )
                )
            }
        }
    }

    private fun multiThreadedProcess(image: WritableImage, num_threads: Int) {
        val executors = Executors.newFixedThreadPool(num_threads)
        val imageList = mutableListOf<WritableImage>()

        for (t in kernel.indices) {
            val layer = WritableImage(
                image.pixelReader,
                image.width.toInt(),
                image.height.toInt()
            )
            imageList.add(layer)
            executors.execute {
                multiThreadTask(t, layer, image.pixelReader)
            }
        }

        executors.shutdown()
        executors.awaitTermination(1, TimeUnit.MINUTES)

        for (x in 0 until image.width.toInt()) {
            for (y in 0 until image.height.toInt()) {
                image.pixelWriter.setColor(x, y, Color(0.0, 0.0, 0.0, 1.0))
            }
        }

        imageList.fold(
            image
        ) { curImage, layer ->
            for (x in 0 until image.width.toInt()) {
                for (y in 0 until image.height.toInt()) {
                    val color = layer.pixelReader.getColor(x, y)
                    val curColor = curImage.pixelReader.getColor(x, y)
                    val newColor = Color(
                        (color.red + curColor.red).coerceIn(0.0, 1.0),
                        (color.green + curColor.green).coerceIn(0.0, 1.0),
                        (color.blue + curColor.blue).coerceIn(0.0, 1.0),
                        1.0
                    )
                    curImage.pixelWriter.setColor(x, y, newColor)
                }
            }
            curImage
        }
    }


    fun convolutionGreyScaleNegative(image: WritableImage): Array<DoubleArray> {
        val deviation = kernel.size / 2
        val width = image.width.toInt()
        val height = image.height.toInt()
        val reader: PixelReader = image.pixelReader
        val original = WritableImage(
            reader,
            width,
            height
        )
        val result = Array(width) { DoubleArray(height) }

        for (y in 0 until height) {
            for (x in 0 until width) {
                var sumR = 0.0
                for (i in -deviation..deviation) {
                    for (j in -deviation..deviation) {
                        val currY = y + i
                        val currX = x + j
                        val factor = kernel[i + deviation][j + deviation]
                        if (currY in 0 until height && currX in 0 until width) {
                            sumR += factor * original.pixelReader.getColor(
                                currX,
                                currY
                            ).red
                        }
                    }
                }
                result[x][y] = sumR
            }
        }

        return result
    }
}