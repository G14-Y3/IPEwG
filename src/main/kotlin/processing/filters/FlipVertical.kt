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
@SerialName("FlipVertical")
class FlipVertical : ImageProcessing {
    override fun process(srcImage: WritableImage, destImage: WritableImage) {
        val numCores = Runtime.getRuntime().availableProcessors()
        multiThreadedProcess(srcImage, destImage, numCores)

    }

    private fun multiThreadedProcess(srcImage: WritableImage, destImage: WritableImage, num_threads: Int) {
        val executorService = Executors.newFixedThreadPool(num_threads)
        val stripeWidth = (srcImage.width / num_threads).roundToInt()

        val reader: PixelReader = srcImage.pixelReader
        val writer: PixelWriter = destImage.pixelWriter

        for (i in 0 until num_threads) {
            val xStart = i * stripeWidth
            val xEnd = minOf((i + 1) * stripeWidth, srcImage.width.toInt())

            executorService.execute {
                for (x in xStart until xEnd) {
                    for (y in 0 until srcImage.height.toInt() / 2) {
                        val colour =
                            reader.getColor(x, srcImage.height.toInt() - y - 1)
                        writer.setColor(
                            x,
                            srcImage.height.toInt() - y - 1,
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

    override fun toString(): String = "Flip Vertical"
}