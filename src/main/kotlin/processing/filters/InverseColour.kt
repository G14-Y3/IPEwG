package processing.filters

import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import processing.ImageProcessing
import processing.multithread.splitImageVertical
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class InverseColour : ImageProcessing {
    override fun process(image: WritableImage) {
        val numCores = Runtime.getRuntime().availableProcessors()
        when (Runtime.getRuntime().availableProcessors()) {
            1 -> singleThreadedProcess(image)
            else -> multiThreadedProcess(
                image,
                numCores
            )
        }
    }

    private fun multiThreadedProcess(image: WritableImage, num_threads: Int) {
        val partitions = splitImageVertical(num_threads, image)
        val executorService = Executors.newFixedThreadPool(num_threads)

        // Divide
        for (partition in partitions) {
            executorService.execute {
                val subImage = partition.image
                val reader = subImage.pixelReader
                val writer = subImage.pixelWriter

                for (x in 0 until subImage.width.toInt()) {
                    for (y in 0 until subImage.height.toInt()) {
                        writer.setColor(x, y, reader.getColor(x, y).invert())
                    }
                }

            }
        }
        executorService.shutdown()
        executorService.awaitTermination(1, TimeUnit.MINUTES)

        // Merge & Conquer
        val writer = image.pixelWriter
        for (partition in partitions) {
            for (x in 0 until partition.image.width.toInt()) {
                for (y in 0 until partition.image.height.toInt()) {
                    writer.setColor(
                        x + partition.x1,
                        y + partition.y1,
                        partition.image.pixelReader.getColor(x, y)
                    )
                }
            }
        }
    }

    private fun singleThreadedProcess(image: WritableImage) {
        val reader: PixelReader = image.pixelReader
        val writer: PixelWriter = image.pixelWriter
        for (x in 0 until image.width.toInt()) {
            for (y in 0 until image.height.toInt()) {
                writer.setColor(x, y, reader.getColor(x, y).invert())
            }
        }
    }

    override fun toString(): String = "Inverse Colour"
}

