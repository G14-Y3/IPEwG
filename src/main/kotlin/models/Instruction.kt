package models

import javafx.scene.image.Image
import javafx.scene.image.WritableImage

// all operations on image are subclasses of Instruction
abstract class Instruction {
    // apply transform on given image
    abstract fun apply(image : WritableImage)

    // when adding the same class of instruction,
    // whether to replace or to remove the previous instruction
    open fun exclusive(): Boolean {
        return true
    }

    // used to check if there are duplicated instructions in the instruction set
    // instructions NEED to override hashCode function to support this
    override fun equals(other: Any?): Boolean {
        return other.hashCode() == this.hashCode()
    }

    abstract override fun hashCode(): Int
}