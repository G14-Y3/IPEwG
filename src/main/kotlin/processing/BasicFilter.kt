package processing

import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage

class BasicFilter: ImageProcessing {
    fun greyscaleFilter(image: WritableImage) {
        val reader: PixelReader = image.pixelReader
        val writer: PixelWriter = image.pixelWriter

        for (i in 0..image.width.toInt()) {
            for (j in 0..image.height.toInt()) {
                writer.setColor(i, j, reader.getColor(i, j).grayscale())
            }
        }
    }
}