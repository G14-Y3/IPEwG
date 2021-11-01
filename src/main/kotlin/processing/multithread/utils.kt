package processing.multithread

import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import kotlin.math.roundToInt

data class ImagePartition(
    val x1: Int,
    val y1: Int,
    val image: WritableImage
)

fun splitImageVertical(n_splits: Int, image: Image): List<ImagePartition> =
    mutableListOf<ImagePartition>().apply {
        val stripeWidth = (image.width / n_splits).roundToInt()

        for (i in 0 until n_splits) {
            val imagePartition = WritableImage(
                image.pixelReader,
                i * stripeWidth,
                0,
                minOf(stripeWidth, image.width.toInt() - i * stripeWidth),
                image.height.toInt()
            )
            add(
                ImagePartition(
                    i * stripeWidth,
                    0,
                    imagePartition
                )
            )
        }
    }

fun splitImageHorizontal(n_splits: Int, image: Image): List<ImagePartition> =
    mutableListOf<ImagePartition>().apply {
        val stripeHeight = (image.height / n_splits).roundToInt()

        for (i in 0 until n_splits) {
            val imagePartition = WritableImage(
                image.pixelReader,
                0,
                i * stripeHeight,
                image.width.toInt(),
                minOf(stripeHeight, image.height.toInt() - i * stripeHeight)
            )
            add(
                ImagePartition(
                    0,
                    i * stripeHeight,
                    imagePartition
                )
            )
        }
    }
