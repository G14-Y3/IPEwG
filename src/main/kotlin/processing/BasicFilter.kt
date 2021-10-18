package processing

import javafx.scene.image.PixelBuffer
import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage

class BasicFilter : ImageProcessing {
    companion object {
        // TODO: reduce code duplication here
        fun greyscaleFilter(image: WritableImage) {
            val reader: PixelReader = image.pixelReader
            val writer: PixelWriter = image.pixelWriter

            for (x in 0 until image.width.toInt()) {
                for (y in 0 until image.height.toInt()) {
                    writer.setColor(x, y, reader.getColor(x, y).grayscale())
                }
            }
        }

        fun inverseColorFilter(image: WritableImage) {
            val reader: PixelReader = image.pixelReader
            val writer: PixelWriter = image.pixelWriter

            for (x in 0 until image.width.toInt()) {
                for (y in 0 until image.height.toInt()) {
                    writer.setColor(x, y, reader.getColor(x, y).invert())
                }
            }
        }

        private fun clone(image: WritableImage): WritableImage {
            return WritableImage(image.pixelReader, image.width.toInt(), image.height.toInt())
        }

        fun mirrorFilter(image: WritableImage) {
            val clonedImage = clone(image)
            val reader: PixelReader = image.pixelReader
            val writer: PixelWriter = image.pixelWriter

            for (x in 0 until image.width.toInt()) {
                for (y in 0 until image.height.toInt()) {
                    clonedImage.pixelWriter.setColor(
                        image.width.toInt() - x - 1,
                        y,
                        reader.getColor(x, y)
                    )
                }
            }
            for (x in 0 until image.width.toInt()) {
                for (y in 0 until image.height.toInt()) {
                    writer.setColor(x, y, clonedImage.pixelReader.getColor(x, y))
                }
            }
        }
    }
}