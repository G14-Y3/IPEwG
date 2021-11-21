package processing.depthestimation

import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import net.mahdilamb.colormap.Colormaps
import org.pytorch.*
import processing.ImageProcessing

class DepthEstimation(val type: DepthEstimationModel): ImageProcessing {
    private val modelType = mapOf(
        DepthEstimationModel.NYU to "./src/main/resources/depth_estimation_model/nyu.pt",
        DepthEstimationModel.KITTI to "\"./src/main/resources/depth_estimation_model/kitti.pt"
    )

    private lateinit var depthImage: WritableImage

    companion object {
        val MAX_DEPTH = 1000.0
        val MIN_DEPTH = 10.0
    }

    val mod: Module = Module.load(modelType[type])

    override fun process(image: WritableImage) {
        val reader = image.pixelReader

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
        depthImage = WritableImage(new_w, new_h)

        val colormap = Colormaps.fluidColormap(Colormaps.get("Viridis"));

        for (i in 0 until new_w) {
            for (j in 0 until new_h) {
                val r = output[(i * new_h + j)]
                val clipped = clip(MAX_DEPTH / r, MIN_DEPTH, MAX_DEPTH) / MAX_DEPTH
                val final_color = colormap.get(clipped)
                val color = Color(final_color.red / 255.0, final_color.green / 255.0, final_color.blue / 255.0, reader.getColor(j, i).opacity)
                depthImage.pixelWriter.setColor(j, i, color)
            }
        }
    }

    private fun clip(value: Double, min: Double, max: Double): Double {
        return if (value < min) {
            min
        } else if (value > max) {
            max
        } else {
            value
        }
    }

    public fun get_depth_image(): WritableImage {
        return depthImage
    }
}