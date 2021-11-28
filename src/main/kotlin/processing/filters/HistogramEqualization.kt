package processing.filters

import CIELab
import javafx.scene.image.PixelReader
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import processing.ImageProcessing
import javax.swing.text.html.HTML.Attribute.N
import kotlin.math.max

/**
 * Reference: http://colormine.org/convert/rgb-to-lab
 */
enum class LabColor(override val range: Int) : ColorSpace {
    L(101) {
        override fun getter(pixel: Color): Double {
            return CIELab.rgb2CIELab(pixel)[0].toDouble() / range
        }

        override fun setter(pixel: Color, value: Double): Color {
            val colorvalue = CIELab.rgb2CIELab(pixel)
            colorvalue[0] = value.toFloat() * range
            return CIELab.CIELab2RGB(colorvalue)
        }
    },
    A(256) {
        override fun getter(pixel: Color): Double {
            return (CIELab.rgb2CIELab(pixel)[1].toDouble() / range).coerceIn(0.0, 1.0)
        }

        override fun setter(pixel: Color, value: Double): Color {
            val colorvalue = CIELab.rgb2CIELab(pixel)
            colorvalue[1] = value.toFloat() * range
            return CIELab.CIELab2RGB(colorvalue)
        }
    },
    B(256) {
        override fun getter(pixel: Color): Double {
            return (CIELab.rgb2CIELab(pixel)[2].toDouble() / range).coerceIn(0.0, 1.0)
        }

        override fun setter(pixel: Color, value: Double): Color {
            val colorvalue = CIELab.rgb2CIELab(pixel)
            colorvalue[2] = value.toFloat() * range
            return CIELab.CIELab2RGB(colorvalue)
        }
    };
    override fun toString(): String {
        return "LabColour" + super.toString()
    }
}

/**
 * Dummy class representing Histogram Equalization using GrayScaled value
 */
class GrayScaleColorSpace(override val range: Int = 256) : ColorSpace {
    override fun getter(pixel: Color): Double {
        return pixel.grayscale().red
    }

    override fun setter(pixel: Color, value: Double): Color {
        return Color.color(value, value, value)
    }

    override fun toString(): String {
        return "GrayScale"
    }
}

@Serializable
@SerialName("HistogramEqualization")
class HistogramEqualization(private val space: ColorSpace): ImageProcessing {
    private val cdf: Array<Int> = Array(space.range) {0}
    private val pdf: Array<Int> = Array(space.range) {0}
    private val pixelMap: Array<Double> = Array(space.range) {0.0}

    override fun process(image: WritableImage) {

        // 1. generate pdf of each pixel value, ASSUME image is in gray scale,
        //    element at position i in pdf is count of pixel value i in the image
        val reader : PixelReader = image.pixelReader
        val height = image.height.toInt()
        val width = image.width.toInt()

        for (i in 0 until height) {
            for (j in 0 until width) {
                // in this nested for loop, CDF is generated as PDF, transfer to cdf in the next step
                val pixel = reader.getColor(j, i)
                val pixelVal = space.getter(pixel) * (space.range - 1)
                pdf[pixelVal.toInt()] += 1
            }
        }

        // 2. generate cdf w.r.t. pdf and record the count of pixel value which is the smallest among all pixel values
        histogramEqualize(height * width)

        // 3. write back to image
        val writer = image.pixelWriter
        for (i in 0 until height) {
            for (j in 0 until width) {
                val originPixel = reader.getColor(j, i)
                val readPixelVal = space.getter(originPixel) * (space.range - 1)
                val pixelVal = pixelMap[readPixelVal.toInt()] / space.range
                writer.setColor(j, i, space.setter(originPixel, pixelVal))
            }
        }
    }

    /**
     * Equalize the distribution recorded in pdf field
     * @param pixelCnt: number of pixels in the image
     * Operations:
     *   Overwrite private field cdf by using data from field pdf
     *   Overwrite private field pixelMap to map each original pixel value to new value
     * Reference:
     *   https://opentextbc.ca/graphicdesign/chapter/4-4-lab-colour-space-and-delta-e-measurements/
     *   https://en.wikipedia.org/wiki/Histogram_equalization
     */
    private fun histogramEqualize(pixelCnt: Int) {
        var cdfMin = 0
        cdf[0] = pdf[0]
        for (i in 1 until space.range) {
            cdf[i] = cdf[i-1] + pdf[i]
            if (cdf[i-1] == 0) {
                cdfMin = cdf[i]
            }
        }

        for (i in 0 until space.range) {
            pixelMap[i] = (cdf[i] - cdfMin).toDouble() / (pixelCnt - cdfMin) * (space.range - 2) + 1
        }
    }

    override fun toString(): String {
        return "Histogram Equalization in $space space"
    }

    fun getOriginalCdf(): Array<Int> {
        return cdf
    }

    fun getResultCdf(): Array<Int> {
        val newCount = Array(space.range) {0}

        for(i in 0 until space.range)
            newCount[pixelMap[i].toInt()] = cdf[i]

        for(i in 1 until space.range)
            newCount[i] = max(newCount[i], newCount[i-1])

        return newCount
    }
}
