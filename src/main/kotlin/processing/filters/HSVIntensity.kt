package processing.filters

import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.HSVType
import processing.ImageProcessing
import processing.multithread.splitImageHorizontal
import processing.multithread.splitImageVertical
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class HSVIntensity(private val factor: Double, private val type: HSVType) :
    ImageProcessing {
    override fun process(image: WritableImage) {
        val numCores = Runtime.getRuntime().availableProcessors()
        multiThreadedProcess(image, numCores)
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
                        val color: Color = reader.getColor(x, y)
                        val newColor: Color = when (type) {
                            HSVType.H -> color.deriveColor(
                                (factor - 1) * 180,
                                1.0,
                                1.0,
                                1.0
                            )
                            HSVType.S -> color.deriveColor(
                                0.0,
                                factor,
                                1.0,
                                1.0
                            )
                            HSVType.V -> color.deriveColor(
                                0.0,
                                1.0,
                                factor,
                                1.0
                            )
                        }
                        writer.setColor(x, y, newColor)
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


    override fun toString(): String = "${type}=${(factor * 100 - 100).toInt()}%"
}
