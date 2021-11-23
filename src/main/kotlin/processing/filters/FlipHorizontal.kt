package processing.filters

import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import processing.ImageProcessing
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@Serializable
@SerialName("FlipHorizontal")
class FlipHorizontal : ImageProcessing {
    override fun process(image: WritableImage) {
        val numCores = Runtime.getRuntime().availableProcessors()
        multiThreadedProcess(image, numCores)
    }

    private fun multiThreadedProcess(image: WritableImage, num_threads: Int) {
        val executorService = Executors.newFixedThreadPool(num_threads)
        val stripeWidth = (image.height / num_threads).roundToInt()

        val reader: PixelReader = image.pixelReader
        val writer: PixelWriter = image.pixelWriter

        for (i in 0 until num_threads) {
            val yStart = i * stripeWidth
            val yEnd = minOf((i + 1) * stripeWidth, image.height.toInt())

            executorService.execute {
                for (x in 0 until image.width.toInt() / 2) {
                    for (y in yStart until yEnd) {
                        val colour =
                            reader.getColor(image.width.toInt() - x - 1, y)
                        writer.setColor(
                            image.width.toInt() - x - 1,
                            y,
                            reader.getColor(x, y)
                        )
                        writer.setColor(x, y, colour)
                    }
                }
            }
        }
        executorService.shutdown()
        executorService.awaitTermination(1, TimeUnit.MINUTES)
    }

    override fun toString(): String = "Flip Horizontal"
}