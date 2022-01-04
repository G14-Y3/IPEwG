package processing.resample

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.max
import kotlin.math.min

private const val lagrange = "Lagrange"

@Serializable
@SerialName("$lagrange-Params")
data class LagrangeParams(val degree: Int) : Params {
    override fun toString(): String = "(degree $degree)"
}

@Serializable
@SerialName(lagrange)
class LagrangeInterpolation(
    private val srcW: Int,
    private val srcH: Int,
    private val tarW: Int,
    private val tarH: Int,
    private val params: Params?,
) : Interpolation {
    private var extent: Int

    init {
        if (params !is LagrangeParams) {
            throw IllegalArgumentException("Expect LagrangeParams, get ${params?.javaClass}")
        }
        extent = params.degree + 1
    }

    private val leftExtent: Int = (extent - 1) / 2
    private val rightExtent: Int = extent - 1 - leftExtent

    override fun getPixel(reader: RGBAReader, x: Int, y: Int): RGBA = let {
        val srcX: Double = x.toDouble() * srcW / tarW
        val srcY: Double = y.toDouble() * srcH / tarH

        // interpolate in horizontal direction
        val interpolateOnX: RGBAReader = { x, y ->
            interpolate(
                supportCoordinatesOnX(x, y),
                reader,
            ) { srcX - it.first.toDouble() }
        }

        // interpolate in vertical direction
        interpolate(supportCoordinatesOnY(srcX.toInt(), srcY.toInt()), interpolateOnX) {
            srcY - it.second.toDouble()
        }
    }

    // Ref: https://github.com/scipy/scipy/blob/v1.7.1/scipy/interpolate/interpolate.py#L25-L82
    private fun interpolate(
        points: List<Pair<Int, Int>>,
        reader: RGBAReader,
        dist: (Pair<Int, Int>) -> Double,
    ): RGBA {
        var result: RGBA = RGBA.addIdentity

        points.forEachIndexed { i, (xi, yi) ->
            var curr = 1.0
            points.forEachIndexed { j, (xj, yj) ->
                if (i != j) {
                    curr *= dist(Pair(xj, yj)) / (xi - xj + yi - yj)
                }
            }
            result += curr * reader(xi, yi)
        }

        return result
    }

    private fun supportCoordinatesOnX(x: Int, y: Int): List<Pair<Int, Int>> =
        (max(0, x - leftExtent)..min(srcW - 1, x + rightExtent))
            .zip(generateSequence { y }.asIterable())

    private fun supportCoordinatesOnY(x: Int, y: Int): List<Pair<Int, Int>> =
        generateSequence { x }.asIterable()
            .zip(max(0, y - leftExtent)..min(srcH - 1, y + rightExtent))

    override fun toString(): String = "$lagrange $params"
}
