package processing.filters

import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import processing.ImageProcessing
import processing.multithread.splitImageVertical
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class FlipVertical : ImageProcessing {
    override fun process(image: WritableImage) {
        val numCores = Runtime.getRuntime().availableProcessors()
        multiThreadedProcess(image, numCores)

    }

    private fun multiThreadedProcess(image: WritableImage, num_threads: Int) {
        val executorService = Executors.newFixedThreadPool(num_threads)
        val stripeWidth = (image.width / num_threads).roundToInt()

        val reader: PixelReader = image.pixelReader
        val writer: PixelWriter = image.pixelWriter

        for (i in 0 until num_threads) {
            val xStart = i * stripeWidth
            val xEnd = minOf((i + 1) * stripeWidth, image.width.toInt())

            executorService.execute {
                for (x in xStart until xEnd) {
                    for (y in 0 until image.height.toInt() / 2) {
                        val colour =
                            reader.getColor(x, image.height.toInt() - y - 1)
                        writer.setColor(
                            x,
                            image.height.toInt() - y - 1,
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