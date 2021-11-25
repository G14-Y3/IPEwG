package processing.denoise

import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import processing.ImageProcessing

class Denoise(val denoiseMethod: DenoiseMethod, val noise: Double): ImageProcessing {
    private val methodToModel =
        mapOf(DenoiseMethod.RIDNET to "./src/main/resources/denoise_model/ridnet.pt",
              DenoiseMethod.DRUNET to "./src/main/resources/denoise_model/drunet.pt")
    val model: Module = Module.load(methodToModel[denoiseMethod])

    override fun process(image: WritableImage) {
        when (denoiseMethod) {
            DenoiseMethod.RIDNET -> ridnet(image)
            DenoiseMethod.DRUNET -> drunet(image)
        }
    }

    private fun drunet(image: WritableImage) {
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
                pixels[0][i][j] = reader.getColor(i, j).red
                pixels[1][i][j] = reader.getColor(i, j).green
                pixels[2][i][j] = reader.getColor(i, j).blue
            }
        }
        val dimension = 4 * h * w
        val buf = FloatArray(dimension)
        for (i in 0 until w) {
            for (j in 0 until h) {
                buf[j * w + i] = pixels[0][i][j].toFloat()
                buf[j * w + i + h*w] = pixels[1][i][j].toFloat()
                buf[j * w + i + h*w*2] = pixels[2][i][j].toFloat()
                buf[j * w + i + h*w*3] = (noise / 255.0).toFloat()
            }
        }

        val data = Tensor.fromBlob(buf, longArrayOf(1, 4, h.toLong(), w.toLong()))
        val result = model.forward(IValue.from(data))
        val output = result.toTensor().dataAsFloatArray

        for (i in 0 until w) {
            for (j in 0 until h) {
                val r = clamp(output[j * w + i].toDouble())
                val g = clamp(output[j * w + i + h * w].toDouble())
                val b = clamp(output[j * w + i + h * w * 2].toDouble())
                val color = Color.color(r, g, b)
                writer.setColor(i, j, color)

            }
        }
    }

    private fun ridnet(image: WritableImage) {
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