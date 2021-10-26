package processing.filters

import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.ImageProcessing
import java.lang.Math.toDegrees
import kotlin.math.atan2
import kotlin.math.sqrt

class EdgeDetection : ImageProcessing {
    override fun process(image: WritableImage) {
        Grayscale().process(image)
        GaussianBlur(2).process(image)


        // Apply Sobel operator for basic edge detection
        val horizontalKernel = arrayOf(arrayOf(1.0, 2.0, 1.0), arrayOf(0.0, 0.0, 0.0), arrayOf(-1.0, -2.0, -1.0))
        val horizontal = Convolution(horizontalKernel).convolutionGreyScaleNegative(image)

        val verticalKernel = arrayOf(arrayOf(1.0, 0.0, -1.0), arrayOf(2.0, 0.0, -2.0), arrayOf(1.0, 0.0, -1.0))
        val vertical = Convolution(verticalKernel).convolutionGreyScaleNegative(image)
        val direction = Array(image.width.toInt()) { DoubleArray(image.height.toInt()) }

        for (x in 0 until image.width.toInt()) {
            for (y in 0 until image.height.toInt()) {
                val horizontalPx = horizontal[x][y]
                val verticalPx = vertical[x][y]
                val color = sqrt(horizontalPx * horizontalPx + verticalPx * verticalPx).coerceIn(0.0, 1.0)
                image.pixelWriter.setColor(x, y, Color.color(color, color, color))
                val degree = toDegrees(atan2(verticalPx, horizontalPx))
                direction[x][y] = if (degree < 0) degree + 180 else degree
            }
        }
        nonMaxSuppression(image, direction)
    }

    private fun nonMaxSuppression(input: WritableImage, direction: Array<DoubleArray>) {
        val original = WritableImage(
            input.pixelReader,
            input.width.toInt(),
            input.height.toInt()
        )

        for (x in 1 until original.width.toInt() - 1) {
            for (y in 1 until original.height.toInt() - 1) {
                var q: Double
                var r: Double
                val angle = direction[x][y]
                when {
                    (0 <= angle && angle < 22.5)  -> {
                        q = original.pixelReader.getColor(x, y + 1).brightness
                        r = original.pixelReader.getColor(x, y - 1).brightness
                    }
                    (22.5 <= angle && angle < 67.5)  -> {
                        q = original.pixelReader.getColor(x + 1, y - 1).brightness
                        r = original.pixelReader.getColor(x - 1, y + 1).brightness
                    }
                    (67.5 <= angle && angle < 112.5)  -> {
                        q = original.pixelReader.getColor(x + 1, y).brightness
                        r = original.pixelReader.getColor(x - 1, y).brightness
                    }
                    (112.5 <= angle && angle < 157.5)  -> {
                        q = original.pixelReader.getColor(x - 1, y - 1).brightness
                        r = original.pixelReader.getColor(x + 1, y + 1).brightness
                    }
                    else -> {
                        q = original.pixelReader.getColor(x, y + 1).brightness
                        r = original.pixelReader.getColor(x, y - 1).brightness
                    }
                }
                val curr = original.pixelReader.getColor(x, y).brightness
                val color = if (curr >= q && curr >= r) curr else 0.0
                input.pixelWriter.setColor(x, y, Color.color(color, color, color))
            }
        }



    }

    override fun toString(): String = "Edge detection"
}