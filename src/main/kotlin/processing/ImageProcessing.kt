package processing

import javafx.scene.image.WritableImage

enum class FreqProcessType {Idle}
enum class FreqProcessRange {HighPass, LowPass}
enum class RGBType { R, G, B }

enum class HSVType {
    H, // Hue
    S, // Saturation
    V // Value (Brightness)
}

interface ImageProcessing {

    fun process(image: WritableImage)
}

enum class BlurType {
    BOX, LENS, GAUSSIAN, MOTION_0, MOTION_45, MOTION_90, MOTION_135
}
