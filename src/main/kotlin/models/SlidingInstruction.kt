package models

import javafx.scene.image.WritableImage

// parent class for operations that involve a sliding bar, e.g. RGB, brightness, ...
abstract class SlidingInstruction {

    abstract fun apply(image : WritableImage)

    var slidingValue : Double = 0.0

    fun setSlidingVal(v : Double) {
        slidingValue = v
    }

}