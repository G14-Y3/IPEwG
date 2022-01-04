package processing.resample

import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import processing.ImageProcessing
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

private const val name = "Resample"

@Serializable
@SerialName(name)
class Resample(
    private val sourceWidth: Int,
    private val sourceHeight: Int,
    private val targetWidth: Int,
    private val targetHeight: Int,
    private val fromSRGB: Boolean,
    private val params: Params?,
    private val method: ResampleMethod,
) : ImageProcessing {
    private val interpolator: Interpolation = method.create(
        sourceWidth,
        sourceHeight,
        targetWidth,
        targetHeight,
        params,
    )

    override fun process(srcImage: WritableImage, destImage: WritableImage) {
        val numCores = Runtime.getRuntime().availableProcessors()
        multiThreadedProcess(srcImage, destImage, numCores)
    }

    private fun multiThreadedProcess(
        srcImage: WritableImage,
        destImage: WritableImage,
        num_threads: Int
    ) {
        val executorService = Executors.newFixedThreadPool(num_threads)
        val stripeWidth = (destImage.height / num_threads).roundToInt()

        val writer: PixelWriter = destImage.pixelWriter
        val reader: RGBAReader = { x, y ->
            if (fromSRGB)
                srcImage.pixelReader.getColor(x, y).toRGBAFromSRGB()
            else srcImage.pixelReader.getColor(x, y).toRGBA()
        }

        for (i in 0 until num_threads) {
            val yStart = i * stripeWidth
            val yEnd = minOf((i + 1) * stripeWidth, destImage.height.toInt())

            executorService.execute {
                for (y in yStart until yEnd) {
                    for (x in 0 until destImage.width.toInt()) {
                        writer.setColor(
                            x,
                            y,
                            if (fromSRGB) interpolator.getPixel(reader, x, y).sRGB
                            else interpolator.getPixel(reader, x, y).color
                        )
                    }
                }
            }
        }
        executorService.shutdown()
        executorService.awaitTermination(1, TimeUnit.MINUTES)
    }

    override fun toString(): String = "${if (fromSRGB) "Gamma-aware" else ""} $name ($interpolator)"
}
