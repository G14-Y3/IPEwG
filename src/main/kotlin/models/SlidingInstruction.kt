package models

// parent class for operations that involve a sliding bar, e.g. RGB, brightness, ...
abstract class SlidingInstruction : Instruction() {
    var slidingValue : Double = 0.0

    fun setSlidingVal(v : Double) {
        slidingValue = v
    }

}