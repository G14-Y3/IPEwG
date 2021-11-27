package processing.styletransfer

import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import processing.ImageProcessing

@Serializable
@SerialName("NeuralStyleTransfer")
class NeuralStyleTransfer(val style: NeuralStyles) : ImageProcessing {

    private val styleToPath = mapOf(
        NeuralStyles.VAN_GOGH to "./src/main/resources/style_transfer_model/van_gogh.pt",
        NeuralStyles.PICASSO to "./src/main/resources/style_transfer_model/picasso.pt",
        NeuralStyles.UKIYOE to "./src/main/resources/style_transfer_model/japan.pt",
        NeuralStyles.AUTUMN to "./src/main/resources/style_transfer_model/autumn.pt",
        NeuralStyles.ABSTRACT to "./src/main/resources/style_transfer_model/abstract.pt",
        NeuralStyles.GOOGLE to "./src/main/resources/style_transfer_model/google.pt"
    )

    @Transient
    val mod: Module = Module.load(styleToPath[style])

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
                pixels[0][i][j] = reader.getColor(i, j).red
                pixels[1][i][j] = reader.getColor(i, j).green
                pixels[2][i][j] = reader.getColor(i, j).blue
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
        val result = mod.forward(IValue.from(data))
        val output = result.toTensor().dataAsFloatArray

        for (i in 0 until w) {
            for (j in 0 until h) {
                val r = output[j * w + i]
                val g = output[j * w + i + h * w]
                val b = output[j * w + i + h * w * 2]
                val color = Color(r.toDouble(), g.toDouble(), b.toDouble(), reader.getColor(i, j).opacity)
                writer.setColor(i, j, color)
            }
        }
    }

    override fun toString(): String = "Neural Style Transfer: $style"
}