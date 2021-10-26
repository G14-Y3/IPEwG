package processing.filters

import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.ImageProcessing
import kotlin.math.sqrt

class EdgeDetection : ImageProcessing {
    override fun process(image: WritableImage) {
        Grayscale().process(image)
        GaussianBlur(2).process(image)

        val horizontalKernel = arrayOf(arrayOf(1.0, 2.0, 1.0), arrayOf(0.0, 0.0, 0.0), arrayOf(-1.0, -2.0, -1.0))
        val horizontal = Convolution(horizontalKernel).convolutionGreyScaleNegative(image)

        val verticalKernel = arrayOf(arrayOf(1.0, 0.0, -1.0), arrayOf(2.0, 0.0, -2.0), arrayOf(1.0, 0.0, -1.0))
        val vertical = Convolution(verticalKernel).convolutionGreyScaleNegative(image)

        for (x in 0 until image.width.toInt()) {
            for (y in 0 until image.height.toInt()) {
                val horizontalPx = horizontal[x][y]
                val verticalPx = vertical[x][y]
                val color = sqrt(horizontalPx * horizontalPx + verticalPx * verticalPx).coerceIn(0.0, 1.0)
                image.pixelWriter.setColor(x, y, Color.color(color, color, color))
            }
        }
    }

    override fun toString(): String = "Edge detection"
}