package processing.filters

import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.ImageProcessing

enum class FalseColoringMethod {
    ENHANCEMENT, TRANSFORM
}

class FalseColoring(val coloringMethod: FalseColoringMethod) : ImageProcessing {
    override fun process(srcImage: WritableImage, destImage: WritableImage) {
        when (coloringMethod) {
            FalseColoringMethod.ENHANCEMENT -> enhance(srcImage, destImage)
            FalseColoringMethod.TRANSFORM -> transform(srcImage, destImage)
        }
    }

    private fun enhance(srcImage: WritableImage, destImage: WritableImage) {
        fun getR(value: Double): Double {
            return if (value < 127) 0.0 else if (value > 191) 255.0 else (value - 127.0) * 4.0 - 1.0
        }

        fun getG(value: Double): Double {
            return if (value < 64) 4.0 * value else if (value > 191) (255.0 - (value - 191.0) * 4.0) else 255.0
        }

        fun getB(value: Double): Double {
            return if (value < 64) return 255.0 else if (value > 127.0) 0.0 else 255.0 - (value - 63.0) * 4.0
        }
        
        val reader = srcImage.pixelReader
        val writer = destImage.pixelWriter

        for (x in 0 until srcImage.width.toInt()) {
            for (y in 0 until srcImage.height.toInt()) {
                val r = reader.getColor(x, y).red
                val g = reader.getColor(x, y).green
                val b = reader.getColor(x, y).blue
                val gray = ((r + g + b) / 3.0) * 255.0
                val newr = clamp(getR(gray) / 255.0)
                val newg = clamp(getG(gray) / 255.0)
                val newb = clamp(getB(gray) / 255.0)
                writer.setColor(x, y, Color.color(newb, newg, newr))
            }
        }
    }

    private fun transform(srcImage: WritableImage, destImage: WritableImage) {
        val falseColorArray = listOf(listOf(0, 51, 0), listOf(0, 51, 102), listOf(51, 51, 102), listOf(51, 102, 51),
        listOf(51, 51, 153), listOf(102, 51, 102), listOf(153, 153, 0), listOf(51, 102, 153),
        listOf(153, 102, 51), listOf(153, 204, 102), listOf(204, 153, 102), listOf(102, 204, 102),
        listOf(153, 204, 153), listOf(204, 204, 102), listOf(204, 255, 204), listOf(255, 255, 204))

        val reader = srcImage.pixelReader
        val writer = destImage.pixelWriter

        for (x in 0 until srcImage.width.toInt()) {
            for (y in 0 until srcImage.height.toInt()) {
                val r = reader.getColor(x, y).red
                val g = reader.getColor(x, y).green
                val b = reader.getColor(x, y).blue
                val gray = ((r + g + b) / 3.0) * 255.0
                val falseColor = falseColorArray[(gray / 16.0).toInt()]
                val color = Color.color(falseColor[2] / 255.0, falseColor[1] / 255.0, falseColor[0] / 255.0)
                writer.setColor(x, y, color)
            }
        }
    }

    private fun clamp(value: Double): Double {
        return if (value < 0) 0.0 else if (value > 1) 1.0 else value
    }
}