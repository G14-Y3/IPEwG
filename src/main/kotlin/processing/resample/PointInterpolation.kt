package processing.resample

import javafx.scene.image.PixelReader
import javafx.scene.paint.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val name = "PointInterpolation"

@Serializable
@SerialName(name)
class PointInterpolation(
    private val sourceWidth: Int,
    private val sourceHeight: Int,
    private val targetWidth: Int,
    private val targetHeight: Int,
    private val params: Params?,
) : Interpolation {
    override fun getPixel(reader: PixelReader, x: Int, y: Int): RGBA {
        // the corresponding coordinate to the pixel in the source
        // should `floor` but not `round`.
        // Since pixels are meant to have "width", consider the alignment of two signals.
        val srcX: Int = x * sourceWidth / targetWidth
        val srcY: Int = y * sourceHeight / targetHeight
        val color: Color = reader.getColor(srcX, srcY)

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
    private val sourceWidth: Int,
    private val sourceHeight: Int,
    private val targetWidth: Int,
    private val targetHeight: Int,
    private val params: Params?,
) : Interpolation {
    private val scaleX = targetWidth / sourceWidth
    private val scaleY = targetHeight / sourceHeight

    init {
        if (targetWidth != scaleX * sourceWidth
            || targetHeight != scaleY * sourceHeight
        ) {
            throw IllegalArgumentException(
                "Cannot deduce correct scaling factor! "
                    + "Target width and height must be of multiples of the source. "
                    + "$scaleX * $sourceWidth != $targetWidth "
                    + "OR $scaleY * $sourceHeight != $targetHeight."
            )
        }
    }

    override fun getPixel(reader: PixelReader, x: Int, y: Int): RGBA =
        if (x % scaleX == 0 && y % scaleY == 0) RGBA.fromColor(
            reader.getColor(
                x / scaleX,
                y / scaleY,
            )
        ) else RGBA(.0, .0, .0, 1.0)

    override fun toString(): String = "pointWithZeros ($scaleX, $scaleY)x"
}