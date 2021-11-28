package processing.resample

import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import processing.ImageProcessing
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

// srcW, srcH, tarW, tarH
enum class ResampleMethod(val create: (Int, Int, Int, Int, Params?) -> Interpolation) {
    Point(::PointInterpolation),
    PointWithZeros(::PointWithZeros),

    // Box(:BoxInterpolation), // only show difference to Point with subsampling
    Bilinear(::BilinearInterpolation),
    Bicubic(::BicubicInterpolation),
}

// an (assumed) linear RGBA pixel
// This is used to avoid Clamping.
// Ref: https://web.archive.org/web/20190214200551/http://entropymine.com/imageworsener/clamp-int/
data class RGBA(val R: Double, val G: Double, val B: Double, val A: Double) {
    companion object {
        val addIdentity = RGBA(.0, .0, .0, .0)
        fun fromColor(x: Color): RGBA = RGBA(x.red, x.green, x.blue, x.opacity)
    }

    // Clamped.
    val color get() = Color(clamp(R), clamp(G), clamp(B), clamp(A))

    // clamp to .0 - 1.0 (incl.)
    private fun clamp(x: Double): Double =
        if (x <= .0) .0 else (if (x >= 1.0) 1.0 else x)

    operator fun plus(other: RGBA) = RGBA(R + other.R, G + other.G, B + other.B, A + other.A)
    operator fun times(scalar: Double) = RGBA(scalar * R, scalar * G, scalar * B, scalar * A)
    operator fun times(scalar: Int) = RGBA(scalar * R, scalar * G, scalar * B, scalar * A)
}

fun Color.toRGBA() = RGBA.fromColor(this)
operator fun Double.times(vector: RGBA) = vector * this
operator fun Int.times(vector: RGBA) = vector * this

// 4 is an adjustable threshold
fun Double.equalsDelta(other: Double) = abs(this - other) < max(Math.ulp(this), Math.ulp(other)) * 4

private const val name = "Resample"

@Serializable
@SerialName(name)
class Resample(
    private val sourceWidth: Int,
    private val sourceHeight: Int,
    private val targetWidth: Int,
    private val targetHeight: Int,
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
        val reader: PixelReader = srcImage.pixelReader

        for (i in 0 until num_threads) {
            val yStart = i * stripeWidth
            val yEnd = minOf((i + 1) * stripeWidth, destImage.height.toInt())

            executorService.execute {
                for (y in yStart until yEnd) {
                    for (x in 0 until destImage.width.toInt()) {
                        writer.setColor(
                            x,
                            y,
                            interpolator.getPixel(reader, x, y).color
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