package processing.filters

import javafx.scene.image.WritableImage
import processing.HSVType
import processing.ImageProcessing
import processing.RGBType
import processing.filters.blur.BoxBlur
import processing.filters.blur.GaussianBlur
import processing.filters.blur.LensBlur
import processing.filters.blur.MotionBlur

class Adjustment(private val properties: Map<String, Double>) : ImageProcessing {
    var string: String = ""

    override fun process(image: WritableImage) {
        for ((k, v) in properties) {
            val adjustment = when (k) {
                "R" -> RGBIntensity(v, RGBType.R)
                "G" -> RGBIntensity(v, RGBType.G)
                "B" -> RGBIntensity(v, RGBType.B)
                "H" -> HSVIntensity(v, HSVType.H)
                "S" -> HSVIntensity(v, HSVType.S)
                "V" -> HSVIntensity(v, HSVType.V)
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