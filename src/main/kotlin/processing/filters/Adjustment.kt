package processing.filters

import javafx.scene.image.WritableImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import processing.ImageProcessing
import processing.filters.blur.BoxBlur
import processing.filters.blur.GaussianBlur
import processing.filters.blur.LensBlur
import processing.filters.blur.MotionBlur
import javafx.scene.paint.Color
import processing.rotation.Rotation

enum class RGBType(override val range: Int) : ColorSpace {
    R(256) {
        override fun getter(pixel: Color): Double {
            return pixel.red
        }

        override fun setter(pixel: Color, value: Double): Color {
            return Color.color(value, pixel.green, pixel.blue)
        }
    },
    G(256) {
        override fun getter(pixel: Color): Double {
            return pixel.green
        }

        override fun setter(pixel: Color, value: Double): Color {
            return Color.color(pixel.red, value, pixel.blue)
        }
    },
    B(256) {
        override fun getter(pixel: Color): Double {
            return pixel.blue
        }

        override fun setter(pixel: Color, value: Double): Color {
            return Color.color(pixel.red, pixel.green, value)
        }
    }
}
enum class HSVType(override val range: Int) : ColorSpace {
    H(360) { // Hue
        override fun getter(pixel: Color): Double {
            return pixel.hue / range.toDouble()
        }

        override fun setter(pixel: Color, value: Double): Color {
            return pixel.deriveColor((value * range.toDouble()) / pixel.hue, 1.0, 1.0, 1.0)
        }
    },
    S(256) { // Saturation
        override fun getter(pixel: Color): Double {
            return pixel.saturation
        }

        override fun setter(pixel: Color, value: Double): Color {
            return pixel.deriveColor(1.0, value / pixel.saturation, 1.0, 1.0)
        }
    },
    V(256) { // Value (Brightness)
        override fun getter(pixel: Color): Double {
            return pixel.brightness
        }

        override fun setter(pixel: Color, value: Double): Color {
            return pixel.deriveColor(1.0, 1.0, value / pixel.brightness, 1.0)
        }
    }
}

enum class BlurType {
    BOX, LENS, GAUSSIAN, MOTION_0, MOTION_45, MOTION_90, MOTION_135
}

@Serializable
@SerialName("Adjustment")
class Adjustment(private val properties: Map<String, Double>) : ImageProcessing {
    @Transient
    var string: String = ""

    override fun process(image: WritableImage) {
        for ((k, v) in properties) {
            val adjustment = when (k) {
                // RGB filters
                "R" -> RGBIntensity(v, RGBType.R)
                "G" -> RGBIntensity(v, RGBType.G)
                "B" -> RGBIntensity(v, RGBType.B)
                // HSV filters
                "H" -> HSVIntensity(v, HSVType.H)
                "S" -> HSVIntensity(v, HSVType.S)
                "V" -> HSVIntensity(v, HSVType.V)
                // Blur filters
                "BOX" -> BoxBlur(v.toInt())
                "LENS" -> LensBlur(v.toInt())
                "GAUSSIAN" -> GaussianBlur(v.toInt())
                "MOTION_0" -> MotionBlur(v.toInt(), 0.0)
                "MOTION_45" -> MotionBlur(v.toInt(), 45.0)
                "MOTION_90" -> MotionBlur(v.toInt(), 90.0)
                "MOTION_135" -> MotionBlur(v.toInt(), 135.0)
                "BLACK_AND_WHITE" -> BlackAndWhite(v)
                // Rotation
                "ROTATION" -> Rotation(v)
                // Contrast
                "CONTRAST" -> Contrast(v)
                else -> null
            }
            string += adjustment.toString() + " "
            adjustment?.process(image)
        }
    }

    override fun toString(): String {
        return string
    }
}