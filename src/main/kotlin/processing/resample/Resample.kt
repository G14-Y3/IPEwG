package processing.resample

import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import processing.ImageProcessing
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

enum class ResampleMethod(val create: (Double, Double, WritableImage) -> Interpolation) {
    Point(::PointInterpolation),
}

// an (assumed) linear RGBA pixel
// This is used to avoid Clamping.
// Ref: https://web.archive.org/web/20190214200551/http://entropymine.com/imageworsener/clamp-int/
class RGBA(var R: Double, var G: Double, var B: Double, var A: Double) {
    companion object {
        fun fromColor(x: Color): RGBA = RGBA(x.red, x.green, x.blue, x.opacity)
    }

    // Clamped.
    val color get() = Color(clamp(R), clamp(G), clamp(B), clamp(A))

    // clamp to .0 - 1.0 (incl.)
    private fun clamp(x: Double): Double =
        if (x <= .0) .0 else (if (x >= 1.0) 1.0 else x)
}

private const val name = "Resample"

@Serializable
@SerialName(name)
class Resample(
    private val targetWidth: Int,
    private val targetHeight: Int,
    private val method: ResampleMethod,
) : ImageProcessing {
    private lateinit var interpolator: Interpolation

    @Contextual
    val targetImage: WritableImage = WritableImage(targetWidth, targetHeight)

    override fun process(image: WritableImage) {
        interpolator = method.create(targetImage.width, targetImage.height, image)
        val numCores = Runtime.getRuntime().availableProcessors()
        multiThreadedProcess(targetImage, numCores)
    }

    private fun multiThreadedProcess(image: WritableImage, num_threads: Int) {
        val executorService = Executors.newFixedThreadPool(num_threads)
        val stripeWidth = (image.height / num_threads).roundToInt()

        val writer: PixelWriter = image.pixelWriter

        for (i in 0 until num_threads) {
            val yStart = i * stripeWidth
            val yEnd = minOf((i + 1) * stripeWidth, image.height.toInt())

            executorService.execute {
                for (y in yStart until yEnd) {
                    for (x in 0 until image.width.toInt()) {
                        writer.setColor(
                            x,
                            y,
                            interpolator.getPixel(x, y).color
                        )
                    }
                }
            }
        }
        executorService.shutdown()
        executorService.awaitTermination(1, TimeUnit.MINUTES)
    }

    override fun toString(): String = "$name($interpolator)"
}