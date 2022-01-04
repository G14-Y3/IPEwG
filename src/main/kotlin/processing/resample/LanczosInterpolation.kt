package processing.resample

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.*

private const val lanczos = "Lanczos"

@Serializable
@SerialName("$lanczos-Params")
data class LanczosParams(val taps: Int, val EWA: Boolean) : Params {
    override fun toString(): String = "$taps ${if (EWA) "EWA" else "2-pass"}"
}

// Ref: https://en.wikipedia.org/wiki/Lanczos_resampling#Definition
// Ref: https://www.pbr-book.org/3ed-2018/Sampling_and_Reconstruction/Image_Reconstruction#x1-WindowedSincFilter
@Serializable
@SerialName(lanczos)
class LanczosInterpolation(
    private val srcW: Int,
    private val srcH: Int,
    private val tarW: Int,
    private val tarH: Int,
    private val params: Params?,
) : Interpolation {
    private var taps: Int
    private var useEWA: Boolean

    init {
        if (params !is LanczosParams) {
            throw IllegalArgumentException("Expect LanczosParams, get ${params?.javaClass}")
        }
        taps = params.taps
        useEWA = params.EWA
    }

    private val scaleX: Double = tarW.toDouble() / srcW
    private val scaleY: Double = tarH.toDouble() / srcH
    private val radiusX: Double = if (scaleX >= 1.0) taps.toDouble() else taps.toDouble() / scaleX
    private val radiusY: Double = if (scaleY >= 1.0) taps.toDouble() else taps.toDouble() / scaleY

    override fun getPixel(reader: RGBAReader, x: Int, y: Int): RGBA {
        val srcX: Double = x.toDouble() * srcW / tarW
        val srcY: Double = y.toDouble() * srcH / tarH

        val supports = supportCoordinates(srcX.toInt(), srcY.toInt())
            .ifEmpty { listOf(Pair(srcX.toInt(), srcY.toInt())) }
        val kernel = if (useEWA) {
            supports.map { filterAtEWA(srcX - it.first.toDouble(), srcY - it.second.toDouble()) }
        } else {
            supports.map { filterAt(srcX - it.first.toDouble(), srcY - it.second.toDouble()) }
        }
        val fTotal = kernel.sum()
        val fvTotal = supports.zip(kernel)
            .map { reader(it.first.first, it.first.second) * it.second }
            .reduce { acc, rgba -> acc + rgba }

        return fvTotal / fTotal
    }

    // Filter kernel value at ([x], [y]), by a 2-pass separated (Tensor) resampling
    private fun filterAt(x: Double, y: Double): Double =
        filterAt1D(x, radiusX) * filterAt1D(y, radiusY)

    // Filter kernel value at ([x], [y]), by a EWA (Elliptical Weighted Averaging) resampling
    private fun filterAtEWA(x: Double, y: Double): Double = let {
        val radius = directionalEllipticalRadius(x, y, radiusX, radiusY)
        filterAt1D(sqrt(x * x + y * y), radius) * filterAt1D(y, radius)
    }

    private fun filterAt1D(x: Double, radius: Double): Double = windowedSinc(x, radius)

    private fun supportCoordinates(x: Int, y: Int): List<Pair<Int, Int>> =
        (max(0, y - radiusY.toInt())..min(srcH - 1, y + radiusY.toInt())).flatMap { currY ->
            (max(0, x - radiusX.toInt())..min(srcW - 1, x + radiusX.toInt()))
                .zip(generateSequence { currY }.asIterable())
        }

    private fun sinc(x: Double): Double =
        if (x in -1e-7..1e-7) 1.0 else sin(PI * x) / (PI * x)

    // [radius] is the radius of the function
    // [x] is the distance from kernel point to the centre
    private fun windowedSinc(x: Double, radius: Double): Double =
        if (x > radius) .0 else sinc(x) * sinc(x / taps)

    override fun toString(): String = "$lanczos$params"
}