package processing.filters

import javafx.scene.image.ImageView
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
class CNNVisualization(
    val netName: String,
    val imgShape: List<Int>,
    val layerNum: Int,
    val channelNum: List<Int>): ImageProcessing {

    @Transient
    var net: List<String> = listOf()

    override fun process(srcImage: WritableImage, destImage: WritableImage) {
        val srcHeight = srcImage.height.toInt()
        val srcWidth = srcImage.width.toInt()
        loadNet(netName, imgShape)
        net = getLayerPaths()

        val reader = srcImage.pixelReader
        val pixels = Array(3) {
            Array(srcWidth) {
                DoubleArray(
                    srcHeight
                )
            }
        }
        for (i in 0 until srcWidth) {
            for (j in 0 until srcHeight) {
                pixels[0][i][j] = reader.getColor(i, j).red
                pixels[1][i][j] = reader.getColor(i, j).green
                pixels[2][i][j] = reader.getColor(i, j).blue
            }
        }
        val dimension = 3 * srcHeight * srcWidth
        val buf = FloatArray(dimension)
        for (i in 0 until srcWidth) {
            for (j in 0 until srcHeight) {
                buf[j * srcWidth + i] = pixels[0][i][j].toFloat()
                buf[j * srcWidth + i + srcHeight*srcWidth] = pixels[1][i][j].toFloat()
                buf[j * srcWidth + i + srcHeight*srcWidth*2] = pixels[2][i][j].toFloat()
            }
        }

        var data = IValue.from(Tensor.fromBlob(buf, longArrayOf(1, 3, srcHeight.toLong(), srcWidth.toLong())))
        var layerCnt = 0
        for (path in net) {
            val layer = Module.load(path)
            data = layer.forward(data)
            if (layerCnt == layerNum) {
                break
            }
            layerCnt ++
        }

        val metadata = File("./src/main/resources/CNN_split/CNN_traced/.Metadata.log").readLines()
        val tokens = metadata[layerNum + 1].split('|') // +1 for the first line of metadata is not layer information
        val outputWidth = tokens[tokens.size-1].toInt()
        val outputHeight = tokens[tokens.size-2].toInt()
        val outputImage = WritableImage(outputWidth, outputHeight)
        val writer = outputImage.pixelWriter
        val output = data.toTensor().dataAsFloatArray
//        val dimensionIndex = channelNum.reduce { x, y -> x * y }
        val dimensionIndex = 0
        for (i in 0 until outputWidth) {
            val min =
                output.copyOfRange(dimensionIndex * outputWidth * outputHeight, (dimensionIndex + 1) * outputWidth * outputHeight).minOrNull()!!
            val range =
                output.copyOfRange(dimensionIndex * outputWidth * outputHeight, (dimensionIndex + 1) * outputWidth * outputHeight).maxOrNull()!! - min

            for (j in 0 until outputHeight) {
                val pixelVal = output[dimensionIndex * outputWidth * outputHeight + j * outputWidth + i].toDouble()
                val castedVal = ((pixelVal - min) / range).coerceIn(0.0, 1.0)
                val color = Color.color(
                    castedVal,
                    castedVal,
                    castedVal)
                writer.setColor(i, j, color)
            }
        }
//
//        for (i in 0 until outputHeight) {
//            for (j in 0 until 3) {
//                print(output[dimensionIndex * outputWidth * outputHeight + i * outputWidth + j].toString() + ' ')
//            }
//            print("...")
//            for (j in outputWidth - 3 until outputWidth)
//                print(output[dimensionIndex * outputWidth * outputHeight + i * outputWidth + j].toString() + ' ')
//            println()
//        }

        // stretch to dst image size
        val view = ImageView(outputImage)
        view.fitWidth = srcWidth.toDouble()
        view.fitHeight = srcHeight.toDouble()
        val stretchedImage = view.snapshot(null, null)
        val outputReader = stretchedImage.pixelReader
        val dstWriter = destImage.pixelWriter
        for (i in 0 until srcWidth)
            for (j in 0 until srcHeight)
                dstWriter.setColor(i, j, outputReader.getColor(i, j))
    }

    override fun toString(): String {
        return "Visualize CNN layer: $layerNum channel: $channelNum from $netName"
    }

    companion object {
        /**
         * check if net loaded in directory 'resources/CNN_split/CNN_traced' is same net as expectedNet
         *
         * @param expectedNet: path to expectedNet loaded in directory
         * @param expectedNet: path to expectedNet loaded in directory
         */
        fun checkLoaded(expectedNet: String, expectedShape: List<Int>): Boolean {
            val lines = File("./src/main/resources/CNN_split/CNN_traced/.Metadata.log")
                .readLines()
            return lines[0] == expectedNet + ' ' + expectedShape.joinToString()
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
        fun loadNet(path: String, shape: List<Int>): Int {
            if (checkLoaded(path, shape))
                return 0

            val removeProcess = Runtime.getRuntime().exec("rm -r -f ./src/main/resources/CNN_split/CNN_traced")
            removeProcess.waitFor()

            val process = Runtime.getRuntime().exec("python3 ./src/main/resources/CNN_split/CNN_spliter.py $path ${shape.joinToString(separator = ",")}")
            return process.waitFor()
        }
    }

}