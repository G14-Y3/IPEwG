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

enum class RGBType { R, G, B }
enum class HSVType {
    H, // Hue
    S, // Saturation
    V // Value (Brightness)
}

enum class BlurType {
    BOX, LENS, GAUSSIAN, MOTION_0, MOTION_45, MOTION_90, MOTION_135
}

enum class BlendType {
    NORMAL, MULTIPLY, SCREEN, OVERLAY, DARKEN, LIGHTEN, COLOR_DODGE, COLOR_BURN,
    HARD_LIGHT, SOFT_LIGHT, DIFFERENCE, EXCLUSION, HUE, SATURATION, COLOR, LUMINOSITY
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