package processing.filters

import javafx.scene.image.WritableImage
import processing.HSVType
import processing.ImageProcessing
import processing.RGBType

class Adjustment(private val properties: Map<String, Double>) : ImageProcessing {
    override fun process(image: WritableImage) {
        for ((k, v) in properties) {
            when (k) {
                "R" -> RGBIntensity(v, RGBType.R).process(image)
                "G" -> RGBIntensity(v, RGBType.G).process(image)
                "B" -> RGBIntensity(v, RGBType.B).process(image)
                "H" -> HSVIntensity(v, HSVType.H).process(image)
                "S" -> HSVIntensity(v, HSVType.S).process(image)
                "V" -> HSVIntensity(v, HSVType.V).process(image)
                "BOX" -> BoxBlur(v.toInt()).process(image)
                "LENS" -> LensBlur(v.toInt()).process(image)
                "GAUSSIAN" -> GaussianBlur(v.toInt()).process(image)
                "MOTION_0" -> MotionBlur(v.toInt(), 0.0).process(image)
                "MOTION_45" -> MotionBlur(v.toInt(), 45.0).process(image)
                "MOTION_90" -> MotionBlur(v.toInt(), 90.0).process(image)
                "MOTION_135" -> MotionBlur(v.toInt(), 135.0).process(image)
            }
        }
    }

    override fun toString(): String {
        var s = ""

        for ((k, v) in properties) {
            s += when (k) {
                "R" -> RGBIntensity(v, RGBType.R)
                "G" -> RGBIntensity(v, RGBType.G)
                "B" -> RGBIntensity(v, RGBType.B)
                "H" -> HSVIntensity(v, HSVType.H)
                "S" -> HSVIntensity(v, HSVType.S)
                "V" -> HSVIntensity(v, HSVType.V)
                else -> ""
            }
            s += " "
        }

        return s
    }
}