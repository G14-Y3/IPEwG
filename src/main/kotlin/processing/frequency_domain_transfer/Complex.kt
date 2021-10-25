package processing.frequency_domain_transfer

import kotlin.math.PI
import kotlin.math.pow

class Complex(var real: Double = 0.0, var imag: Double = 0.0) {
    infix operator fun plus(y: Complex) = Complex(real + y.real, imag + y.imag)
    infix operator fun minus(y: Complex) = Complex(real - y.real, imag - y.imag)
    infix operator fun times(y: Complex) = Complex(real * real - y.real * y.real, real * y.imag + imag * y.real)
    infix operator fun times(y: Double) = Complex(real * y, imag * y)
    infix operator fun div(y: Double) = Complex(real / y, imag / y)

    // todo: implementation differ with online tutorial, might contain bug
    fun exp() : Complex {
        return Complex(Math.cos(imag), Math.sin(imag)) * Math.exp(real)
    }

    fun pow(n: Double) : Complex {
        var magnitude = Math.sqrt(real.pow(2) + imag.pow(2))
        var theta = Math.atan(imag / real)
        return Complex(Math.cos(theta * n), Math.sin(theta * n)) * magnitude.pow(n)
    }

    companion object {
        // return e^(-2 * pi * i/ n)
        fun negOmegaN(n: Double) : Complex = (Complex(0.0, -2 * PI) / n).exp()
    }

}