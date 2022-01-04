package processing.resample

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.ceil

private const val bicubic = "Bicubic (Mitchell–Netravali)"

@Serializable
@SerialName("$bicubic-Params")
data class BicubicParams(val B: Double, val C: Double) : Params {
    override fun toString(): String = "(B: $B, C: $C)"
}

@Serializable
@SerialName(bicubic)
class BicubicInterpolation(
    private val srcW: Int,
    private val srcH: Int,
    private val tarW: Int,
    private val tarH: Int,
    private val params: Params?,
) : Interpolation {
    private var b: Double
    private var c: Double

    init {
        if (params !is BicubicParams) {
            throw IllegalArgumentException("Expect BicubicParams, get ${params?.javaClass}")
        }
        b = params.B
        c = params.C
    }

    // Ref: https://en.wikipedia.org/wiki/Mitchell%E2%80%93Netravali_filters#Definition

    // Corresponding to the coefficients for d-irrelevant, 1-degree, square and cubic terms,
    // of P0...P3
    val pCoef: List<List<Double>> = listOf(
        listOf(b / 6.0, -b / 3.0 + 1.0, b / 6.0, .0), // d-irrelevant terms
        listOf(-b / 2.0 - c, .0, b / 2.0 + c, .0), // 1-degree
        listOf(b / 2.0 + 2.0 * c, 2.0 * b + c - 3.0, -5.0 / 2.0 * b - 2.0 * c + 3.0, c), // square
        // cubic
        listOf(-b / 6.0 - c, -3.0 / 2.0 * b - c + 2.0, 3.0 / 2.0 * b + c - 2.0, b / 6.0 + c),
    )

    private val scaleX: Double = tarW.toDouble() / srcW
    private val scaleY: Double = tarH.toDouble() / srcH

    override fun getPixel(reader: RGBAReader, x: Int, y: Int): RGBA {
        // Ref: https://en.wikipedia.org/wiki/Mitchell–Netravali_filters
        val x1: Int = x * srcW / tarW
        val xInc: Int = ceil(1.0 / scaleX).toInt()
        val y1: Int = y * srcH / tarH
        val yInc: Int = ceil(1.0 / scaleY).toInt()

        // Dist to P1
        val xDist: Double = x.toDouble() / scaleX - x1
        val yDist: Double = y.toDouble() / scaleY - y1

        // interpolate in horizontal direction
        val pVertical = getSupports(y1, x1, yInc, srcH, srcW) { _y, _x ->
            interpolate(xDist, getSupports(_x, _y, xInc, srcW, srcH, reader))
        }

        // interpolate in vertical direction
        return interpolate(yDist, pVertical)
    }

    // Must have 4 points of `P`
    // Ref: https://en.wikipedia.org/wiki/Linear_interpolation#Linear_interpolation_between_two_known_points
    private fun interpolate(d: Double, P: List<RGBA>): RGBA {
        var dPow = 1.0
        var accumulated: RGBA = RGBA.addIdentity
        var coefTotal = .0

        for (i in 0..3) {
            coefTotal += dPow * pCoef[i].sum()
            accumulated += dPow * pCoef[i].zip(P)
                .fold(RGBA.addIdentity) { acc, pair -> acc + pair.first * pair.second }
            dPow *= d
        }

        return accumulated / coefTotal
    }

    // Always return 4 points in the same horizontal direction
    // take [x], [y] as in src coordinate space.
    // If [y] exceeds [maxY], returns an empty list.
    private fun getSupports(
        x: Int,
        y: Int,
        xInc: Int,
        maxX: Int,
        maxY: Int,
        reader: RGBAReader,
    ): List<RGBA> {
        if (y >= maxY) {
            return listOf()
        }

        val list: MutableList<RGBA> = mutableListOf()
        for (i in -1..2) {
            val xPos = x + i * xInc
            if (xPos in 0 until maxX) {
                list.add(reader(xPos, y))
            } else if (list.isNotEmpty()) {
                list.add(list.last())
            }
        }

        // fill up extra points before p1, the direct previous point
        if (list.size < 4) {
            val first = list.first()
            list.addAll(0, List(4 - list.size) { first })
        }

        return list
    }

    override fun toString(): String = "$bicubic $params"
}
