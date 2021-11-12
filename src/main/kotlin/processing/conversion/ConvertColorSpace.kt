package processing.conversion

import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import processing.ImageProcessing
import processing.filters.HSVType
import processing.multithread.splitImageVertical
import java.lang.StrictMath.pow
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

enum class ColorSpaceType { SRGB, LinearRGB }

enum class ColorChannels { Red, Green, Blue, Alpha }

@Serializable
@SerialName("ColorSpace")
class ColorSpace(private val source: ColorSpaceType, private val target: ColorSpaceType) :
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
                        val linear: Color = when (source) {
                            ColorSpaceType.LinearRGB -> color
                            ColorSpaceType.SRGB -> channelWiseConversion(
                                color,
                                ::sRGBToLinearByChannel,
                            )
                        }
                        val newColor: Color = when (target) {
                            ColorSpaceType.LinearRGB -> linear
                            ColorSpaceType.SRGB -> channelWiseConversion(
                                color,
                                ::linearTosRGBByChannel,
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

    private fun sRGBToLinearByChannel(value: Double, channel: ColorChannels) =
        if (channel == ColorChannels.Alpha) value else {
            if (value <= 0.04045) value / 12.92
            else pow((value + 0.055) / 1.055, 2.4)
        }

    private fun linearTosRGBByChannel(value: Double, channel: ColorChannels) =
        if (channel == ColorChannels.Alpha) value else {
            if (value <= 0.04045) value * 12.92
            else 1.055 * pow(value, 1.0 / 2.4) - 0.055
        }

    private fun channelWiseConversion(
        color: Color,
        func: (Double, ColorChannels) -> Double
    ): Color = Color(
        func(color.red, ColorChannels.Red),
        func(color.green, ColorChannels.Green),
        func(color.blue, ColorChannels.Blue),
        func(color.opacity, ColorChannels.Alpha),
    )

    override fun toString(): String = "Conversion: $source => $target"
}
