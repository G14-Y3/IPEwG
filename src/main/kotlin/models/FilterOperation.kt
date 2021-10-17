package models

import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import processing.BasicFilter

class GreyScale : Instruction() {
    override fun apply(image: WritableImage) {
        BasicFilter.greyscaleFilter(image)
    }

    override fun hashCode(): Int {
        return "GrayScale".hashCode()
    }
}

class InverseColour : Instruction() {
    override fun apply(image: WritableImage) {
        BasicFilter.inverseColorFilter(image)
    }

    override fun hashCode(): Int {
        return ("InverseColour").hashCode()
    }
}

class Mirror : Instruction() {
    override fun apply(image: WritableImage) {
        TODO("Not yet implemented")
    }

    override fun hashCode(): Int {
        return ("Mirror").hashCode()
    }

}

enum class RGB_type {R, G, B}
class RGB(val type : RGB_type) : SlidingInstruction() {

    override fun apply(image: WritableImage) {
        println(type.toString() + " " + this.slidingValue)
        BasicFilter.RGBColorFilter(image, this.slidingValue, type)
    }

    override fun exclusive(): Boolean {
        return false
    }

    override fun toString(): String {
        return "RGB$type${this.slidingValue}"
    }

    override fun hashCode(): Int {
        return ("RGB$type").hashCode()
    }
}

class Brightness : SlidingInstruction() {

    override fun apply(image: WritableImage) {
        TODO("Not yet implemented")
    }

    override fun hashCode(): Int {
        return ("Brightness").hashCode()
    }

}

class Contrast : SlidingInstruction() {
    override fun apply(image: WritableImage) {
        TODO("Not yet implemented")
    }

    override fun hashCode(): Int {
        return ("Contrast").hashCode()
    }
}

class Satuation : SlidingInstruction() {
    override fun apply(image: WritableImage) {
        TODO("Not yet implemented")
    }

    override fun hashCode(): Int {
        return ("Satuation").hashCode()
    }
}