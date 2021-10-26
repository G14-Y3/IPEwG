package processing.frequency_domain_transfer

import javafx.scene.image.PixelReader
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import processing.FreqProcessRange
import processing.FreqProcessType
import processing.ImageProcessing
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class FrequencyFilters(private val type : FreqProcessType, private val range : FreqProcessRange) : ImageProcessing{

    override fun process(image: WritableImage) {
        var matrix = Array(2) {Complex()}
        for (i in 0..1) {
            matrix[i] = Complex(i.toDouble())
        }
        print("original:")
        for (v in matrix) {
            print("$v ")
        }
        println()
        print("new: ")
        matrix = _fft(matrix, Complex.posOmegaN(2.0), 1.0)
        for (v in matrix) {
            print("$v ")
        }
        println()
    }

    fun process_real(image: WritableImage) {
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
            matrix[i] = fft2(matrix[i])
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

    // todo: design a more informative to string
    override fun toString(): String {
        return "frequency filter with $type and $range"
    }

    // fft on 2 dimensional matrix
    // Reference: http://rosettacode.org/wiki/Fast_Fourier_transform#Kotlin
    private fun fft2(matrix: Array<Array<Complex>>): Array<Array<Complex>> =
        _fft2(matrix, false)
    private fun ifft2(matrix: Array<Array<Complex>>): Array<Array<Complex>> =
        _fft2(matrix, true)

    private fun _fft2(matrix: Array<Array<Complex>>, isIfft: Boolean): Array<Array<Complex>> {
        var omega = Complex.negOmegaN(matrix.size.toDouble())
        var scalar = 2.0
        if (isIfft) {
            omega = Complex.negOmegaN(matrix.size.toDouble())
            scalar = 1.0
        }
        // apply fft to each column
        for (i in 0 until matrix[0].size) {
            var column = Array(matrix.size) {
                matrix[i][it]
            }
            column = _fft(column, omega, scalar)
            for (j in matrix.indices) {
                matrix[j][i] = column[j]
            }
        }

        // apply fft to each row
        omega = Complex.posOmegaN(matrix.size.toDouble())
        if (isIfft) {
            omega = Complex.posOmegaN(matrix.size.toDouble())
        }
        for (i in matrix.indices) {
            matrix[i] = _fft(matrix[i], omega, scalar)
        }
        return matrix
    }

    // return fft result on given array, ASSUMED the length of array is power of 2
    // References: https://www.bilibili.com/video/BV1za411F76U
    // todo: remove the constrain of length of array can only be power of 2
    // omega = e^(-2 * pi * i / n) or e^(2 * pi * i / n)
    private fun _fft(array: Array<Complex>, omega: Complex, scalar: Double): Array<Complex> {
        println("omega$omega")
        val length = array.size
        if (length == 1) {
            return array
        }
//        var evens = Array(length / 2) {array[2 * it]}
//        var odds = Array(length / 2) {array[2 * it + 1]}
        var (evens, odds) = Pair(emptyArray<Complex>(), emptyArray<Complex>())
        for (i in array.indices)
            if (i % 2 == 0) evens += array[i]
            else odds += array[i]
        evens = _fft(evens, omega, scalar)
        odds = _fft(odds, omega, scalar)

//        val concatResult = Array(length) {Complex()}
//        for (i in 0 until length / 2) {
//            concatResult[i] =
//                evens[i] / scalar + odds[i] * omega.pow(i.toDouble()) / scalar
//            concatResult[i + length / 2] =
//                evens[i] / scalar - odds[i] * omega.pow(i.toDouble()) / scalar
//        }
//
//        return concatResult
        val pairs = (0 until length / 2).map {
            val offset = (Complex(0.0, 2.0) * (java.lang.Math.PI * it / length)).exp() * odds[it] / scalar
            val base = evens[it] / scalar
            Pair(base + offset, base - offset)
        }
        var (left, right) = Pair(emptyArray<Complex>(), emptyArray<Complex>())
        for ((l, r) in pairs) { left += l; right += r }
        return left + right
    }

}