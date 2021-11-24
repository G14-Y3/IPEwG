package processing.filters

import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import processing.ImageProcessing

class Denoise: ImageProcessing {
    val model = Module.load("./src/main/resources/denoise_model/saved.pt")

    override fun process(image: WritableImage) {
        val reader = image.pixelReader
        val writer = image.pixelWriter
        val h = image.height.toInt()
        val w = image.width.toInt()
        val pixels = Array(3) {
            Array(w) {
                DoubleArray(
                    h
                )
            }
        }
        for (i in 0 until w) {
            for (j in 0 until h) {
                pixels[0][i][j] = reader.getColor(i, j).red * 255
                pixels[1][i][j] = reader.getColor(i, j).green * 255
                pixels[2][i][j] = reader.getColor(i, j).blue * 255
            }
        }
        val dimension = 3 * h * w
        val buf = FloatArray(dimension)
        for (i in 0 until w) {
            for (j in 0 until h) {
                buf[j * w + i] = pixels[0][i][j].toFloat()
                buf[j * w + i + h*w] = pixels[1][i][j].toFloat()
                buf[j * w + i + h*w*2] = pixels[2][i][j].toFloat()
            }
        }

        val data = Tensor.fromBlob(buf, longArrayOf(1, 3, h.toLong(), w.toLong()))
        val result = model.forward(IValue.from(data))
        val output = result.toTensor().dataAsFloatArray

        for (i in 0 until w) {
            for (j in 0 until h) {
                val r = clamp(output[j * w + i] / 255.0)
                val g = clamp(output[j * w + i + h * w] / 255.0)
                val b = clamp(output[j * w + i + h * w * 2] / 255.0)
                val color = Color(r, g, b, reader.getColor(i, j).opacity)
                writer.setColor(i, j, color)
            }
        }
    }

    private fun clamp(value: Double): Double {
        return if (value < 0) 0.0 else if (value > 1) 1.0 else value
    }
}