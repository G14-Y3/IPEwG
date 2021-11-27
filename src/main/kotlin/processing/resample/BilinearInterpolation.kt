package processing.resample

import javafx.scene.image.PixelReader
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.ceil

private const val bilinear = "Bilinear"

@Serializable
@SerialName(bilinear)
class BilinearInterpolation(
    private val srcW: Int,
    private val srcH: Int,
    private val tarW: Int,
    private val tarH: Int,
) : Interpolation {
    private val scaleX: Double = tarW.toDouble() / srcW
    private val scaleY: Double = tarH.toDouble() / srcH

    override fun getPixel(reader: PixelReader, x: Int, y: Int): RGBA {
        // Ref: https://en.wikipedia.org/wiki/Bilinear_interpolation
        val x1: Int = x * srcW / tarW
        val xInc: Int = ceil(1.0 / scaleX).toInt()
        val x2: Int = x1 + xInc
        val y1: Int = y * srcH / tarH
        val yInc: Int = ceil(1.0 / scaleY).toInt()
        val y2: Int = y1 + yInc

        val q11: RGBA = reader.getColor(x1, y1).toRGBA()
        // at corner, repeat
        if (x2 >= srcW && y2 >= srcH) {
            return q11
        }

        // interpolate in horizontal direction
        lateinit var q_1: RGBA
        lateinit var q_2: RGBA
        if (x2 < srcW) {
            // remapped to same coordinate system. tar/src indicates its origin
            // always try to map to the bigger coordinate system
            val scaleDownX = scaleX <= 1.0
            val tarX = if (scaleDownX) x / scaleX else x.toDouble()
            val srcX1 = if (scaleDownX) x1.toDouble() else x1 * scaleX
            val srcX2 = if (scaleDownX) x2.toDouble() else x2 * scaleX

            val q21: RGBA = reader.getColor(x2, y1).toRGBA()
            q_1 = interpolate(tarX, srcX1, srcX2, q11, q21)
            q_2 = if (y2 < srcH) {
                val q12: RGBA = reader.getColor(x1, y2).toRGBA()
                val q22: RGBA = reader.getColor(x2, y2).toRGBA()
                interpolate(tarX, srcX1, srcX2, q12, q22)
            } else {
                q_1
            }
        } else {
            q_1 = q11
            q_2 = if (y2 < srcH) {
                val q12: RGBA = reader.getColor(x1, y2).toRGBA()
                q12
            } else {
                q_1
            }
        }

        // interpolate in vertical direction
        val q: RGBA = if (scaleY <= 1.0) {
            // mapped to SRC coordinate space.
            interpolate(y / scaleY, y1.toDouble(), y2.toDouble(), q_1, q_2)
        } else {
            interpolate(y.toDouble(), y1 * scaleY, y2 * scaleY, q_1, q_2)
        }

        return q
    }

    // Ref: https://en.wikipedia.org/wiki/Linear_interpolation#Linear_interpolation_between_two_known_points
    private fun interpolate(x: Double, x1: Double, x2: Double, y1: RGBA, y2: RGBA): RGBA =
        if (x1.equalsDelta(x2)) y1 else ((x2 - x) / (x2 - x1) * y1 + (x - x1) / (x2 - x1) * y2)

    override fun toString(): String = bilinear
}