package processing.frequency_domain_transfer

class Complex(private var real : Double = 0.0, private var imag : Double = 0.0) {
    infix operator fun plus(y: Complex) = Complex(real + y.real, imag + y.imag)
    infix operator fun minus(y: Complex) = Complex(real - y.real, imag - y.imag)
    infix operator fun times(y: Complex) = Complex(real * real - y.real * y.real, real * y.imag + imag * y.real)
    infix operator fun div(y: Double) = Complex(real / y, imag / y)
    fun setComplex(real: Double) {
        this.real = real
    }

    fun mult(other : Complex) {
        this.real = this.real * this.real - other.real * other.real
        this.imag = this.real * ( other.imag) + this.imag * other.real
    }

    fun add(other : Complex) {
        this.real += other.real
        this.imag += other.imag
    }

}