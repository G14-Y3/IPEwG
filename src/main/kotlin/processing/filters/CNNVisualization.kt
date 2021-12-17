package processing.filters

import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import processing.ImageProcessing
import java.io.File

@Serializable
@SerialName("CNNVisualize")
class CNNVisualization(val netName: String, val layerNum: Int, val channelNum: List<Int>, val height: Int, val width: Int): ImageProcessing {

    @Transient
    var net: List<String> = listOf()

    override fun process(image: WritableImage) {
        if (!checkLoaded(netName)) {
            println("loading new net")
            val exitCode = loadNet(netName)
            println("loading net finish with exit code $exitCode")
        }
        net = getLayerPaths()

        // TODO: use user self defined preprocess method
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

        var data = IValue.from(Tensor.fromBlob(buf, longArrayOf(1, 3, h.toLong(), w.toLong())))
        var layerCnt = 0
        for (path in net) {
            val layer = Module.load(path)
            data = layer.forward(data)
            if (layerCnt == layerNum) {
                break
            }
            layerCnt ++
        }

        val output = data.toTensor().dataAsFloatArray
        val dimensionIndex = channelNum.reduce { x, y -> x * y }
        for (i in 0 until width) {
            val min =
                output.copyOfRange(dimensionIndex * width * height, (dimensionIndex + 1) * width * height).minOrNull()!!
            val range =
                output.copyOfRange(dimensionIndex * width * height, (dimensionIndex + 1) * width * height).maxOrNull()!! - min

            for (j in 0 until height) {
                val pixelVal = output[dimensionIndex * width * height + j * width + i].toDouble()
                val color = Color(
                    (pixelVal - min) / range,
                    (pixelVal - min) / range,
                    (pixelVal - min) / range,
                    reader.getColor(i, j).opacity)
                writer.setColor(i, j, color)
            }
        }
    }

    override fun toString(): String {
        return "Visualize CNN layer: $layerNum channel: $channelNum from $netName"
    }

    companion object {
        /**
         * check if net loaded in directory 'resources/CNN_split/CNN_traced' is same net as expectedNet
         *
         * @param expectedNet: path to expectedNet loaded in directory
         */
        fun checkLoaded(expectedNet: String): Boolean {
            val lines = File("./src/main/resources/CNN_split/CNN_traced/.Metadata.log")
                .readLines()
            return lines[0] == expectedNet
        }

        /**
         * get path for each CNN layer in directory 'resources/CNN_split/CNN_traced'
         *
         * @return: List of string, each is a relative path from root directory of the project, i.e. IPEwG, to the layer
         *          returned in order of the layer position in original loaded net
         */
        fun getLayerPaths(): List<String> {
            val fileNames = mutableListOf<String>()
            File("./src/main/resources/CNN_split/CNN_traced").walk().forEach {
                if (it.isFile && it.name != ".Metadata.log") {
                    fileNames.add(it.toString())
                }
            }
            return fileNames.sorted()
        }

        /**
         * load pytorch CNN to directory 'resources/CNN_split/CNN_traced' directory
         *
         * @param path: path of loaded CNN
         * @return: exit code of loading python script
         */
        fun loadNet(path: String): Int {
            val removeProcess = Runtime.getRuntime().exec("rm -r -f ./src/main/resources/CNN_split/CNN_traced")
            removeProcess.waitFor()

            val process = Runtime.getRuntime().exec("python3 ./src/main/resources/CNN_split/CNN_spliter.py $path ")
            return process.waitFor()
        }
    }

}