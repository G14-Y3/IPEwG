package processing.resample

import javafx.scene.paint.Color
import processing.conversion.ColorChannels
import processing.conversion.linearTosRGBByChannel
import processing.conversion.sRGBToLinearByChannel
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

typealias RGBAReader = (Int, Int) -> RGBA

// srcW, srcH, tarW, tarH
enum class ResampleMethod(val create: (Int, Int, Int, Int, Params?) -> Interpolation) {
    Point(::PointInterpolation),
    PointWithZeros(::PointWithZeros),

    // Box(:BoxInterpolation), // only show difference to Point with subsampling
    Bilinear(::BilinearInterpolation),
    Bicubic(::BicubicInterpolation),
    Lanczos(::LanczosInterpolation),
    Lagrange(::LagrangeInterpolation),
}

// an (assumed) linear RGBA pixel
// This is used to avoid Clamping.
// Ref: https://web.archive.org/web/20190214200551/http://entropymine.com/imageworsener/clamp-int/
data class RGBA(val R: Double, val G: Double, val B: Double, val A: Double) {
    companion object {
        val addIdentity = RGBA(.0, .0, .0, .0)
        val mulIdentity = RGBA(1.0, 1.0, 1.0, 1.0)
        fun fromColor(x: Color, fromSRGB: Boolean = false): RGBA =
            if (fromSRGB)
                RGBA(
                    sRGBToLinearByChannel(x.red, ColorChannels.Red),
                    sRGBToLinearByChannel(x.green, ColorChannels.Green),
                    sRGBToLinearByChannel(x.blue, ColorChannels.Blue),
                    sRGBToLinearByChannel(x.opacity, ColorChannels.Alpha),
                )
                else
            RGBA(x.red, x.green, x.blue, x.opacity)
    }

    // Clamped.
    val color get() = Color(clamp(R), clamp(G), clamp(B), clamp(A))

    val sRGB = Color(
        clamp(linearTosRGBByChannel(R, ColorChannels.Red)),
        clamp(linearTosRGBByChannel(G, ColorChannels.Green)),
        clamp(linearTosRGBByChannel(B, ColorChannels.Blue)),
        clamp(linearTosRGBByChannel(A, ColorChannels.Alpha)),
    )

    // clamp to .0 - 1.0 (incl.)
    private fun clamp(x: Double): Double =
        if (x <= .0) .0 else (if (x >= 1.0) 1.0 else x)

    operator fun plus(other: RGBA) = RGBA(R + other.R, G + other.G, B + other.B, A + other.A)
    operator fun times(scalar: Double) = RGBA(scalar * R, scalar * G, scalar * B, scalar * A)
    operator fun times(scalar: Int) = RGBA(scalar * R, scalar * G, scalar * B, scalar * A)
    operator fun div(scalar: Double) = RGBA(R / scalar, G / scalar, B / scalar, A / scalar)
}

fun Color.toRGBA() = RGBA.fromColor(this)
fun Color.toRGBAFromSRGB() = RGBA.fromColor(this, fromSRGB = true)
operator fun Double.times(vector: RGBA) = vector * this
operator fun Int.times(vector: RGBA) = vector * this

// 4 is an adjustable threshold
fun Double.equalsDelta(other: Double) = abs(this - other) < max(Math.ulp(this), Math.ulp(other)) * 4

fun directionalEllipticalRadius(x: Double, y: Double, radiusX: Double, radiusY: Double): Double {
    if (x in -1e-8..1e-8) {
        return radiusY
    }
    val slope = y / x
    val a = radiusX * radiusX * x * x + radiusY * radiusY
    val c = -radiusX * radiusX * radiusY * radiusY
    val deltaSqr = -4 * a * c // b^2 - 4ac
    val xSqr = deltaSqr / (2 * a * 2 * a) // (-b + delta) / 2a
    val ySqr = xSqr * (slope * slope)
    return sqrt(xSqr + ySqr)
}
