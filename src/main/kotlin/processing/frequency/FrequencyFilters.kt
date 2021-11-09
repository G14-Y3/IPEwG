package processing.frequency

import javafx.scene.image.PixelReader
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.ImageProcessing
import kotlin.math.pow

abstract class FrequencyFilters: ImageProcessing{

    // get filter matrix for selecting different frequency, implementation depends on type of filter
    abstract fun getFilterMatrix(height: Int, width: Int): Array<Array<Double>>

    override fun process(image: WritableImage) {
        // 1. multiplt by (-1)^(i+j) to move top left of image to center
        //    and pad the image to side length of power of 2
        val reader : PixelReader = image.pixelReader
        val oriHeight = image.height.toInt()
        val oriWidth = image.width.toInt()
        val height = nextPow2(oriHeight)
        val width = nextPow2(oriWidth)
        val matrix : Array<Array<Array<Complex>>>
            = Array(3) {Array(height) { Array(width) { Complex() }}}
        for (i in 0 until oriHeight) {
            for (j in 0 until oriWidth) {
                val ratio = (-1.0).pow(i + j)
                matrix[0][i][j].real = reader.getColor(j, i).red * ratio
                matrix[1][i][j].real = reader.getColor(j, i).green * ratio
                matrix[2][i][j].real = reader.getColor(j, i).blue * ratio
            }
        }

        // 2. fft to get image frequencies for each RGB channel
        for (i in 0 .. 2) {
            matrix[i] = fft2(matrix[i])
        }

        // 3. define filter matrix
        val filter = getFilterMatrix(height, width)

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
            matrix[i] = ifft2(matrix[i])
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
        for (x in 0 until oriHeight) {
            for (y in 0 until oriWidth) {
                val newColor: Color = Color.color(
                    matrix[0][x][y].real.coerceIn(0.0, 1.0),
                    matrix[1][x][y].real.coerceIn(0.0, 1.0),
                    matrix[2][x][y].real.coerceIn(0.0, 1.0))
                writer.setColor(y, x, newColor)
            }
        }
    }

    // fft on 2 dimensional matrix
    // Reference: http://rosettacode.org/wiki/Fast_Fourier_transform#Kotlin
    private fun fft2(matrix: Array<Array<Complex>>): Array<Array<Complex>> =
        _fft2(matrix, Complex.posOmegaPower(), 1.0)
    private fun ifft2(matrix: Array<Array<Complex>>): Array<Array<Complex>> =
        _fft2(matrix, Complex.negOmegaPower(), 2.0)

    private fun _fft2(matrix: Array<Array<Complex>>, omegaPower: Complex, scalar: Double): Array<Array<Complex>> {
        // apply fft to each column
        for (i in 0 until matrix[0].size) {
            var column = Array(matrix.size) {
                matrix[it][i]
            }
            column = _fft(column, omegaPower, scalar)
            for (j in matrix.indices) {
                matrix[j][i] = column[j]
            }
        }

        // apply fft to each row
        for (i in matrix.indices) {
            matrix[i] = _fft(matrix[i], omegaPower, scalar)
        }
        return matrix
    }

    // return the value greater or equal to the given value of power 2
    private fun nextPow2(n: Int): Int{
        var power = 1
        while (power < n) {
            power *= 2
        }
        return power
    }

    // return fft result on given array, ASSUMED the length of array is power of 2
    // References: https://www.bilibili.com/video/BV1za411F76U
    // power = (-2 * pi * i) or (2 * pi * i)
    private fun _fft(array: Array<Complex>, power: Complex, scalar: Double): Array<Complex> {
        val length = array.size
        if (length == 1) {
            return array
        }
        var evens = Array(length / 2) {array[2 * it]}
        var odds = Array(length / 2) {array[2 * it + 1]}
        evens = _fft(evens, power, scalar)
        odds = _fft(odds, power, scalar)

        val concatResult = Array(length) { Complex() }
        for (i in 0 until length / 2) {
            concatResult[i] =
                evens[i] / scalar + odds[i] * (power * i.toDouble() / length.toDouble()).exp() / scalar
            concatResult[i + length / 2] =
                evens[i] / scalar - odds[i] * (power * i.toDouble() / length.toDouble()).exp() / scalar
        }

        return concatResult
    }

}