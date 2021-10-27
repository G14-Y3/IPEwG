package processing.frequency_domain_transfer

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow

class Complex(var real: Double = 0.0, var imag: Double = 0.0) {
    infix operator fun plus(y: Complex) = Complex(real + y.real, imag + y.imag)
    infix operator fun minus(y: Complex) = Complex(real - y.real, imag - y.imag)
    infix operator fun times(y: Complex) = Complex(real * y.real - imag * y.imag, real * y.imag + imag * y.real)
    infix operator fun times(y: Double) = Complex(real * y, imag * y)
    infix operator fun div(y: Double) = Complex(real / y, imag / y)

    fun exp() : Complex {
        return Complex(Math.cos(imag), Math.sin(imag)) * Math.exp(real)
    }

    // complex number only support integer power
    fun pow(n: Int) : Complex {
        val magnitude = Math.sqrt(real.pow(2) + imag.pow(2))
        val theta = Math.atan(imag / real)
        return Complex(Math.cos(theta * n), Math.sin(theta * n)) * magnitude.pow(n)
    }

    companion object {
        // return -2 * pi * i
        fun negOmegaPower() : Complex = (Complex(0.0, -2 * PI))
        // return 2 * pi * i
        fun posOmegaPower() : Complex = (Complex(0.0, 2 * PI))
    }

    // toString, mainly for debug use
    override fun toString(): String {
        val a = "%1.3f".format(real)
        val b = "%1.3f".format(imag)
        return "$a+i$b"
    }

}