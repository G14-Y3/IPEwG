package processing.frequency_domain_transfer

import javafx.scene.image.PixelReader
import javafx.scene.image.WritableImage
import processing.FreqProcessRange
import processing.FreqProcessType
import processing.ImageProcessing
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow

class FrequencyFilters(private val type : FreqProcessType, private val range : FreqProcessRange) : ImageProcessing{

    override fun process(image: WritableImage) {
        // 1. multiplt by (-1)^(i+j) to move center
        var reader : PixelReader = image.pixelReader
        var height = image.height.toInt()
        var width = image.width.toInt()
        var matrix : Array<Array<Array<Complex>>>
            = Array(3) {Array(height) { Array(width) {Complex()}}}
        for (i in 0 until height) {
            for (j in 0 until width) {
                val ratio = (-1.0).pow(i + j)
                matrix[0][i][j].setComplex(reader.getColor(i, j).red * ratio)
                matrix[1][i][j].setComplex(reader.getColor(i, j).blue * ratio)
                matrix[2][i][j].setComplex(reader.getColor(i, j).green * ratio)
            }
        }

        // 2. fft to get image frequencies
        for (i in 0 .. 2) {
            matrix[i] = fft2(matrix[i])
        }

        // 3. define filter function

        // 4. apply filter function

        // 5. ifft to get back original image

        // 6. times (-1)^(i+j) to move center back to left top

    }

    // on 2 dimensional matrix
    // Reference: https://nl.mathworks.com/help/matlab/ref/fft2.html#bvhcnas
    private fun fft2(matrix : Array<Array<Complex>>) : Array<Array<Complex>> {
        var height = matrix.size
        var width = matrix[0].size
        for (p in 0 until height) {
            for (q in 0 until width) {
                var pixel_output = Complex()
                for (i in 0 until height) {
                    for (j in 0 until width) {
                        pixel_output.add(matrix[i][j].mult())
                    }
                }
            }
        }
    }
}