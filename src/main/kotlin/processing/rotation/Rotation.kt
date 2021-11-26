package processing.rotation

import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.ImageProcessing
import kotlin.math.*

class Rotation(val angle: Double): ImageProcessing {
    override fun process(image: WritableImage) {
        val reader = image.pixelReader
        val writer = image.pixelWriter
        val width = image.width.toInt()
        val height = image.height.toInt()
        val tempImage = WritableImage(image.pixelReader, width, height)
        val tempReader = tempImage.pixelReader
        val tempWriter = tempImage.pixelWriter

        val midx = width / 2.0
        val midy = height / 2.0

        for (x in 0 until width)
            for (y in 0 until height)
                tempWriter.setColor(x, y, Color.WHITE)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val radian = angle * PI / 180.0
                val color = reader.getColor(x, y)
                val dx = x - midx
                val dy = y - midy
                // shear 1
                val tan = tan(radian / 2.0)
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
                //newx = cos(radian) * (x - midx) - sin(radian) * (y - midy) + midx
                //newy = sin(radian) * (x - midx) + cos(radian) * (y - midy) + midy
                if (newx.toInt() in 0 until width && newy.toInt() in 0 until height) {
                    tempWriter.setColor(newx.toInt(), newy.toInt(), color)
                }
            }
        }

        for (x in 0 until width)
            for (y in 0 until height)
                writer.setColor(x, y, tempReader.getColor(x, y))
    }
}