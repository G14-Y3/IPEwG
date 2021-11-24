package processing.resample

import javafx.scene.image.Image
import javafx.scene.paint.Color
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val name = "PointInterpolation"

@Serializable
@SerialName(name)
class PointInterpolation(
    private val targetWidth: Double,
    private val targetHeight: Double,
    @Contextual private val sourceImage: Image,
) : Interpolation {
    override fun getPixel(x: Int, y: Int): RGBA {
        // the corresponding coordinate to the pixel in the source
        val srcX: Double = x * sourceImage.width / targetWidth
        val srcY: Double = y * sourceImage.height / targetHeight
        // should `floor` but not `round`.
        // Since pixels are meant to have "width", consider the alignment of two signals.
        val xInt: Int = srcX.toInt()
        val yInt: Int = srcY.toInt()
        val color: Color = sourceImage.pixelReader!!.getColor(xInt, yInt)

        return RGBA.fromColor(color)
    }

    override fun toString(): String = name
}