package processing.frequency

import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

@Serializable
@SerialName("FreqFilterGenerator")
abstract class FilterGenerator {

    abstract val filterImageView: ImageView

    // get filter matrix, update the filterImage at the same time
    fun getFilter(height: Int, width: Int): Array<Array<Double>> {
        // new image for the new filter
        val filterImage = WritableImage(height, width)
        val writer = filterImage.pixelWriter

        val filter = Array(height) {Array(width) {0.0} }
        val halfWidth = width / 2.0
        val halfHeight = height / 2.0
        for (x in 0 until height) {
            for (y in 0 until width) {
                val xDist = abs(x - halfHeight) / halfHeight
                val yDist = abs(y - halfWidth) / halfWidth
                val distFromCenter = sqrt(xDist.pow(2) + yDist.pow(2))
                val pixelVal = getFilterPixel(distFromCenter)

                filter[x][y] = pixelVal

                writer.setColor(x, y, Color.color(pixelVal, pixelVal, pixelVal))
            }
        }
        // update filter view to the new filterImage
        filterImageView.image = filterImage
        return filter
    }

    abstract fun getFilterPixel(dist: Double): Double
}