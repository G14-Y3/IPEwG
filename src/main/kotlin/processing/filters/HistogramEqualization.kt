package processing.filters

import javafx.scene.image.PixelReader
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.ImageProcessing

class HistogramEqualization: ImageProcessing {

    override fun process(image: WritableImage) {
        // 0. transfer to grayscale
        Grayscale().process(image)

        // 1. generate pdf of each pixel value, ASSUME image is in gray scale,
        val reader : PixelReader = image.pixelReader
        val height = image.height.toInt()
        val width = image.width.toInt()
        val PIXEL_RANGE = 256
        // element at position i in pdf is count of pixel value i in the image
        val pdf: Array<Int> = Array(PIXEL_RANGE) {0}
        for (i in 0 until height) {
            for (j in 0 until width) {
                // in this nested for loop, CDF is generated as PDF, transfer to cdf in the next step
                val pixel = reader.getColor(j, i)
                val pixelVal = pixel.red * (PIXEL_RANGE - 1) // -1 for avoid index out off bound
                pdf[pixelVal.toInt()] += 1
            }
        }

        // 2. generate cdf w.r.t. pdf and record the count of pixel value which is the smallest among all pixel values
        var cdfMin = 0
        val cdf: Array<Int> = Array(PIXEL_RANGE) {0}
        cdf[0] = pdf[0]
        for (i in 1 until PIXEL_RANGE) {
            cdf[i] = cdf[i-1] + pdf[i]
            if (cdf[i-1] == 0) {
                cdfMin = cdf[i]
            }
        }

        // 3. generate map from original pixel value to new pixel value
        val pixelMap: Array<Double> = Array(PIXEL_RANGE) {0.0}
        for (i in 0 until PIXEL_RANGE) {
            pixelMap[i] = (cdf[i] - cdfMin).toDouble() / (height * width - cdfMin) * (PIXEL_RANGE - 2) + 1
        }

        // 4. write back to image
        val writer = image.pixelWriter
        for (i in 0 until height) {
            for (j in 0 until width) {
                val readPixelVal = reader.getColor(j, i).red * (PIXEL_RANGE - 1)
                val pixelVal = pixelMap[readPixelVal.toInt()] / PIXEL_RANGE
                writer.setColor(j, i, Color.color(pixelVal, pixelVal, pixelVal))
            }
        }
    }

    override fun toString(): String {
        return "Applying Histogram Equalization"
    }

}
