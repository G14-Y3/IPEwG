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
import kotlin.random.Random

private const val CONVERGENCE_THRESHOLD = 1.0
private const val MAX_K_MEANS_PLUS_PLUS_LEVEL = 20

@Serializable
@SerialName("Posterization")
class Posterization(val level: Int) : ImageProcessing {

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
        if (level <= MAX_K_MEANS_PLUS_PLUS_LEVEL) {
            // Do k-means++ for small palettes
            palette.add(allColors.random())
            val probabilities = DoubleArray(allColors.size)
            val allColorsWithIndex = allColors.withIndex().map { (i, color) -> Pair(i, color) }
            for (i in 2..level) {
                allColorsWithIndex.parallelStream().forEach { (index, oldColor) ->
                    val shortestDistance = palette.map { colorDistance(it, oldColor) }.minOrNull()!!
                    probabilities[index] = shortestDistance * shortestDistance
                }
                // Make the probabilities cumulative
                for (index in 1 until probabilities.size) {
                    probabilities[index] += probabilities[index - 1]
                }
                if (probabilities.last() > 0.0) {
                    var index =
                        probabilities.binarySearch(Random.nextDouble(0.0, probabilities.last()))
                    if (index < 0) {
                        index = -index - 1
                    }
                    palette.add(allColors[index])
                } else {
                    palette.add(allColors.random())
                }
            }
        } else {
            // Do simple k-means for large palettes to speed up the computation
            for (i in 1..level) {
                palette.add(allColors.random())
            }
        }
        var newPalette = ArrayList<Color>()
        val clusters = HashMap<Color, MutableList<Color>>()
        while (paletteDistance(palette, newPalette) > CONVERGENCE_THRESHOLD) {
            palette = newPalette
            clusters.clear()
            palette.forEach { clusters[it] = ArrayList() }
            for (oldColor in allColors) {
                val closestColor = palette.minByOrNull { colorDistance(it, oldColor) }
                clusters[closestColor]!!.add(oldColor)
            }
            newPalette = ArrayList(palette.size)
            for ((i, color) in palette.withIndex()) {
                val cluster = clusters[color]!!
                val red = cluster.map { it.red }.average()
                val green = cluster.map { it.green }.average()
                val blue = cluster.map { it.blue }.average()
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
