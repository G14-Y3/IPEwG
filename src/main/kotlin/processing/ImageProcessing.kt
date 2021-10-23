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
