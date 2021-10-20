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
            }
        }
        for ((k, v) in properties) {
            when (k) {
                "H" -> HSVIntensity(v, HSVType.H).process(image)
                "S" -> HSVIntensity(v, HSVType.S).process(image)
                "V" -> HSVIntensity(v, HSVType.V).process(image)
            }
        }
    }
}