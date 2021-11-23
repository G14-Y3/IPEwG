package processing.filters

import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import processing.ImageProcessing
import processing.multithread.splitImageVertical
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Serializable
@SerialName("RGBIntensity")
class RGBIntensity(private val factor: Double, private val rgbType: RGBType) :
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
                        val red = color.red
                        val green = color.green
                        val blue = color.blue
                        val newColor: Color = when (rgbType) {
                            RGBType.R -> Color.color(
                                (red * factor).coerceAtMost(
                                    1.0
                                ), green, blue
                            )
                            RGBType.G -> Color.color(
                                red,
                                (green * factor).coerceAtMost(1.0),
                                blue
                            )
                            RGBType.B -> Color.color(
                                red,
                                green,
                                (blue * factor).coerceAtMost(1.0)
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

    override fun toString(): String = "${rgbType}=${(factor * 100 - 100).toInt()}%"
}