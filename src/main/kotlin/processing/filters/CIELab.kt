import javafx.scene.paint.Color
import java.awt.color.ColorSpace

/**
 * Functions that transfer RGB pixel to LabColor space
 * Reference:
 *   http://www.easyrgb.com/en/math.php
 *   https://coderedirect.com/questions/135797/java-how-to-convert-rgb-color-to-cie-lab
 */
class CIELab internal constructor() : ColorSpace(TYPE_Lab, 3) {
    override fun fromCIEXYZ(colorvalue: FloatArray): FloatArray {
        val l = f(colorvalue[1].toDouble())
        val L = 116.0 * l - 16.0
        val a = 500.0 * (f(colorvalue[0].toDouble()) - l)
        val b = 200.0 * (l - f(colorvalue[2].toDouble()))
        return floatArrayOf(L.toFloat(), a.toFloat(), b.toFloat())
    }

    override fun fromRGB(rgbvalue: FloatArray): FloatArray {
        val xyz = CIEXYZ.fromRGB(rgbvalue)
        return fromCIEXYZ(xyz)
    }

    override fun toCIEXYZ(colorvalue: FloatArray): FloatArray {
        val i = (colorvalue[0] + 16.0) * (1.0 / 116.0)
        val X = fInv(i + colorvalue[1] * (1.0 / 500.0))
        val Y = fInv(i)
        val Z = fInv(i - colorvalue[2] * (1.0 / 200.0))
        return floatArrayOf(X.toFloat(), Y.toFloat(), Z.toFloat())
    }

    override fun toRGB(colorvalue: FloatArray): FloatArray {
        val xyz = toCIEXYZ(colorvalue)
        return CIEXYZ.toRGB(xyz)
    }

    companion object {
        private val converter = CIELab()
        fun rgb2CIELab(pixel: Color): FloatArray {
            val colorvalue = floatArrayOf(
                pixel.red.toFloat(),
                pixel.green.toFloat(),
                pixel.blue.toFloat())
            return converter.fromRGB(colorvalue)
        }

        fun CIELab2RGB(colorvalue: FloatArray): Color {
            val pixelValue = converter.toRGB(colorvalue)
            return Color.color(
                pixelValue[0].toDouble(),
                pixelValue[1].toDouble(),
                pixelValue[2].toDouble())
        }

        private fun f(x: Double): Double {
            return if (x > 216.0 / 24389.0) {
                Math.cbrt(x)
            } else {
                841.0 / 108.0 * x + N
            }
        }

        private fun fInv(x: Double): Double {
            return if (x > 6.0 / 29.0) {
                x * x * x
            } else {
                108.0 / 841.0 * (x - N)
            }
        }

        private val CIEXYZ = getInstance(CS_CIEXYZ)
        private const val N = 4.0 / 29.0
    }
}