package processing.depthestimation

import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import org.pytorch.*
import processing.ImageProcessing

class DepthEstimation(val type: DepthEstimationModel): ImageProcessing {
    private val modelType = mapOf(
        DepthEstimationModel.NYU to "./src/main/resources/depth_estimation_model/nyu.pt",
        DepthEstimationModel.KITTI to "\"./src/main/resources/depth_estimation_model/kitti.pt"
    )

    val mod: Module = Module.load(modelType[type])

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
        val result = mod.forward(IValue.from(data)).toTensor()
        val new_w = result.shape()[2].toInt()
        val new_h = result.shape()[3].toInt()
        val output = result.dataAsFloatArray

        for (i in 0 until new_w) {
            for (j in 0 until new_h) {
                val r = output[(j * new_w + i)] / 8.5
                val color = Color(r, r, r, reader.getColor(i, j).opacity)
                writer.setColor(i, j, color)
            }
        }
    }
}