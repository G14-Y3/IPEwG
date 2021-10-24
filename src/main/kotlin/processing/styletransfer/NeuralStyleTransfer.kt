package processing.styletransfer

import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import processing.ImageProcessing
import java.io.File
import java.util.*

class NeuralStyleTransfer(style: NeuralStyles) : ImageProcessing {
    var mod: Module

    var styleToPath = mapOf(
        NeuralStyles.VAN_GOGH to "./src/main/resources/style_transfer_model/van_gogh.pt",
        NeuralStyles.PICASSO to "./src/main/resources/style_transfer_model/picasso.pth",
        NeuralStyles.UKIYOE to "./src/main/resources/style_transfer_model/japan.pth",
        NeuralStyles.AUTUMN to "./src/main/resources/style_transfer_model/autumn.pth",
        NeuralStyles.ABSTRACT to "./src/main/resources/style_transfer_model/abstract.pth"
    )

    init {
        mod = Module.load(styleToPath.get(style))
    }

    override fun process(image: WritableImage) {
        val reader = image.pixelReader
        val writer = image.pixelWriter
        val h = image.height.toInt()
        val w = image.width.toInt()
        val pixels = Array(3) {
            Array(h) {
                DoubleArray(
                    w
                )
            }
        }
        for (i in 0 until h) {
            for (j in 0 until w) {
                pixels[0][i][j] = reader.getColor(i, j).red
                pixels[1][i][j] = reader.getColor(i, j).green
                pixels[2][i][j] = reader.getColor(i, j).blue
            }
        }
        val dimension = 3 * h * w
        val buf = FloatArray(dimension)
        for (i in 0 until h) {
            for (j in 0 until w) {
                buf[i * w + j * 3] = pixels[0][i][j].toFloat()
                buf[i * w + j * 3 + 1] = pixels[1][i][j].toFloat()
                buf[i * w + j * 3 + 2] = pixels[2][i][j].toFloat()
            }
        }

        val data = Tensor.fromBlob(buf, longArrayOf(1, 3, h.toLong(), w.toLong()))
        val result = mod.forward(IValue.from(data))
        val output = result.toTensor().dataAsFloatArray

        for (i in 0 until h) {
            for (j in 0 until w) {
                val r = output[i * 3 + j * h]
                val g = output[i * 3 + j * h + 1]
                val b = output[i * 3 + j * h + 2]
                val color = Color(r.toDouble(), g.toDouble(), b.toDouble(), reader.getColor(i, j).opacity)
                writer.setColor(i, j, color)
            }
        }
    }
}