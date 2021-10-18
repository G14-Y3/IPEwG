package models

import javafx.scene.image.WritableImage


enum class RGBType {R, G, B}

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