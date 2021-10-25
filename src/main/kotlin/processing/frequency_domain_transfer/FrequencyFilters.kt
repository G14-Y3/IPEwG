package processing.frequency_domain_transfer

import javafx.scene.image.PixelReader
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.FreqProcessRange
import processing.FreqProcessType
import processing.ImageProcessing
import processing.RGBType
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class FrequencyFilters(private val type : FreqProcessType, private val range : FreqProcessRange) : ImageProcessing{

    override fun process(image: WritableImage) {
        // 1. multiplt by (-1)^(i+j) to move center
        val reader : PixelReader = image.pixelReader
        val height = image.height.toInt()
        val width = image.width.toInt()
        val matrix : Array<Array<Array<Complex>>>
            = Array(3) {Array(height) { Array(width) {Complex()}}}
        for (i in 0 until height) {
            for (j in 0 until width) {
                val ratio = (-1.0).pow(i + j)
                matrix[0][i][j].real = reader.getColor(i, j).red * ratio
                matrix[1][i][j].real = reader.getColor(i, j).blue * ratio
                matrix[2][i][j].real = reader.getColor(i, j).green * ratio
            }
        }

        // 2. fft to get image frequencies for each RGB channel
        for (i in 0 .. 2) {
            matrix[i] = dft2(matrix[i])
        }

        // 3. define filter matrix
        // todo: change filter based on request from user, currently default as high pass filter
        val filter = Array(height) {Array(width) {0.0} }
        for (x in 0 until  height) {
            for (y in 0 until  width) {
                val xDist = abs(x - height / 2).toDouble()
                val yDist = abs(y - width / 2).toDouble()
                val distFromCenter = sqrt(xDist.pow(2) + yDist.pow(2))
                if (distFromCenter < width * 0.4) {
                    filter[x][y] = 1.0
                }
            }
        }

        // 4. apply filter to frequency matrix
        for (i in 0 .. 2) {
            for (x in 0 until height) {
                for (y in 0 until width) {
                    matrix[i][x][y] = matrix[i][x][y] * filter[x][y]
                }
            }
        }

        // 5. ifft to get back original image
        for (i in 0 .. 2) {
            matrix[i] = idft2(matrix[i])
        }

        // 6. times (-1)^(i+j) to each position to move center back to left top
        for (i in 0 .. 2) {
            for (x in 0 until height) {
                for (y in 0 until width) {
                    matrix[i][x][y] = matrix[i][x][y] * (-1.0).pow(x + y)
                }
            }
        }

        // 7. write matrix back to image
        val writer = image.pixelWriter
        for (x in 0 until  height) {
            for (y in 0 until  width) {
                val newColor: Color = Color.color(
                    matrix[0][x][y].real.coerceIn(0.0, 1.0),
                    matrix[1][x][y].real.coerceIn(0.0, 1.0),
                    matrix[2][x][y].real.coerceIn(0.0, 1.0))
                writer.setColor(x, y, newColor)
            }
        }
    }

    // dft on 2 dimensional matrix
    // Reference: https://nl.mathworks.com/help/matlab/ref/fft2.html#bvhcnas
    // todo: inplement fft2
    private fun dft2(matrix : Array<Array<Complex>>) : Array<Array<Complex>> {
        val height = matrix.size
        val width = matrix[0].size
        val matrixOutput: Array<Array<Complex>> = Array(height) { Array(width) {Complex()} }
        for (p in 0 until height) {
            for (q in 0 until width) {
                var pixelOutput = Complex()
                for (j in 0 until height) {
                    for (k in 0 until width) {
                        val omegaMjp = Complex.negOmegaN(height.toDouble()).pow(j * p.toDouble())
                        val omegaNkq = Complex.negOmegaN(width.toDouble()).pow(k * q.toDouble())
                        pixelOutput += omegaMjp * omegaNkq * matrix[j][k]
                    }
                }
                matrixOutput[p][q] = pixelOutput
            }
        }
        return matrixOutput
    }

    // idft on 2 dimensional image
    // todo
    private fun idft2(matrix : Array<Array<Complex>>) : Array<Array<Complex>> {
        return matrix
    }
}