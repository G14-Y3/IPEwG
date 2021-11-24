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

private const val pointWithZeros = "Fill with Zeros"

// Copy the pixel when is the direct multiple (thus the top-left corner),
// and fills the rest with 0s.
//
// Note that `targetWidth` and `targetHeight` must be
// multiples of corresponding dimensions in `sourceImage`
@Serializable
@SerialName(pointWithZeros)
class PointWithZeros(
    private val targetWidth: Double,
    private val targetHeight: Double,
    @Contextual private val sourceImage: Image,
) : Interpolation {
    private val scaleX = (targetWidth / sourceImage.width).toInt()
    private val scaleY = (targetHeight / sourceImage.height).toInt()

    init {
        if (targetWidth.toInt() != scaleX * sourceImage.width.toInt()
            || targetHeight.toInt() != scaleY * sourceImage.height.toInt()
        ) {
            throw IllegalArgumentException(
                "Cannot deduce correct scaling factor! "
                    + "Target width and height must be of multiples of the source. "
                    + "$scaleX * ${sourceImage.width.toInt()} != ${targetWidth.toInt()} "
                    + "OR $scaleY * ${sourceImage.height.toInt()} != ${targetHeight.toInt()}."
            )
        }
    }

    override fun getPixel(x: Int, y: Int): RGBA =
        if (x % scaleX == 0 && y % scaleY == 0) RGBA.fromColor(
            sourceImage.pixelReader!!.getColor(
                x / scaleX,
                y / scaleY,
            )
        ) else RGBA(.0, .0, .0, 1.0)

    override fun toString(): String = "pointWithZeros ($scaleX, $scaleY)x"
}