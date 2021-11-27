package processing.rotation

import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.ImageProcessing
import kotlin.math.*

class Rotation(val angle: Double): ImageProcessing {
    override fun process(srcImage: WritableImage, destImage: WritableImage) {
        val reader = srcImage.pixelReader
        val writer = destImage.pixelWriter
        val width = srcImage.width.toInt()
        val height = srcImage.height.toInt()
        val tempImage = WritableImage(srcImage.pixelReader, width, height)
        val tempReader = tempImage.pixelReader
        val tempWriter = tempImage.pixelWriter

        val midx = width / 2.0
        val midy = height / 2.0

        for (x in 0 until width)
            for (y in 0 until height)
                tempWriter.setColor(x, y, Color.WHITE)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val radian = if (angle >= 90 && angle < 270) (angle - 180) * PI / 180.0 else angle * PI / 180.0
                val color = reader.getColor(x, y)
                val dx = x - midx
                val dy = y - midy
                // original version. This produces lots of mini-holes inside the rotated
                // newx = cos(radian) * (x - midx) - sin(radian) * (y - midy) + midx
                // newy = sin(radian) * (x - midx) + cos(radian) * (y - midy) + midy

                // shear 1
                val tan = tan( radian / 2.0)
                var newx = round(dx - dy * tan)
                var newy = dy

                // shear 2
                newy = round(newx * sin(radian) + newy)

                // shear 3
                newx = round(newx - newy * tan)
                newx += midx
                newy += midy
                newx = round(newx)
                newy = round(newy)

                if (newx.toInt() in 0 until width && newy.toInt() in 0 until height) {
                    if (angle > 90 && angle < 270) {
                        newy = height - newy - 1
                        newx = width - newx - 1
                    }
                    tempWriter.setColor(newx.toInt(), newy.toInt(), color)
                }
            }
        }

        for (x in 0 until width)
            for (y in 0 until height)
                writer.setColor(x, y, tempReader.getColor(x, y))
    }
}