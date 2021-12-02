package processing.filters

import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import processing.ImageProcessing
import kotlin.math.pow
import kotlin.math.sqrt

@Serializable
@SerialName("Posterize")
class Posterize(val level: Int) : ImageProcessing {
    override fun process(srcImage: WritableImage, destImage: WritableImage) {
        if (level == 0) {
            return
        }
        val width = srcImage.width.toInt()
        val height = srcImage.height.toInt()
        val reader: PixelReader = srcImage.pixelReader
        val writer: PixelWriter = destImage.pixelWriter
        val allColors = ArrayList<Color>()
        for (y in 0 until height) {
            for (x in 0 until width) {
                allColors.add(reader.getColor(x, y))
            }
        }
        var palette = ArrayList<Color>()
        var newPalette = ArrayList<Color>()
        for (i in 1..level) {
            palette.add(reader.getColor((0 until width).random(), (0 until height).random()))
        }
        val clusters = HashMap<Color, MutableList<Color>>()
        while (paletteDistance(palette, newPalette) > 1.0) {
            palette = newPalette
            clusters.clear()
            for (oldColor in allColors) {
                val closestColor = palette.minByOrNull { colorDistance(it, oldColor) }!!
                if (!clusters.contains(closestColor)) {
                    clusters[closestColor] = ArrayList()
                }
                clusters[closestColor]!!.add(oldColor)
            }
            newPalette = ArrayList(palette.size)
            for ((i, color) in palette.withIndex()) {
                val red = clusters[color]!!.map { it.red }.average()
                val green = clusters[color]!!.map { it.green }.average()
                val blue = clusters[color]!!.map { it.blue }.average()
                newPalette[i] = Color(red, green, blue, 1.0)
            }
        }
        for (y in 0 until height) {
            for (x in 0 until width) {
                val oldColor = reader.getColor(x, y)
                val closestColor = palette.minByOrNull { colorDistance(it, oldColor) }!!
                writer.setColor(x, y, closestColor)
            }
        }
    }

    // Calculate the color distance using redmean approximation
    private fun colorDistance(colorA: Color, colorB: Color): Double {
        val rMean = (colorA.red + colorB.red) / 2
        val r = colorA.red - colorB.red
        val g = colorA.green - colorB.green
        val b = colorA.blue - colorB.blue
        return sqrt((2 + rMean) * r * r + 4 * g * g + (3 - rMean) * b * b)
    }

    private fun paletteDistance(paletteA: List<Color>, paletteB: List<Color>): Double {
        return sqrt(
            paletteA.zip(paletteB).map { (colorA, colorB) -> colorDistance(colorA, colorB).pow(2) }
                .average()
        )
    }

    override fun toString(): String {
        return "Posterize level=level$"
    }
}
