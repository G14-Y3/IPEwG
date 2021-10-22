package processing

import javafx.scene.image.WritableImage

enum class RGBType {R, G, B}
enum class FreqProcessType {Idle}
enum class FreqProcessRange {HighPass, LowPass}

interface ImageProcessing {

    fun process(image: WritableImage)
}
