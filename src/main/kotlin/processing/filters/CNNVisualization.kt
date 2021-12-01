package processing.filters

import javafx.scene.image.WritableImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.pytorch.Module
import processing.ImageProcessing
import java.io.File

@Serializable
@SerialName("CNNVisualize")
class CNNVisualization(val netName: String, val layerNum: Int): ImageProcessing {

    @Transient
    val net: List<String> = getLayerPaths()

    override fun process(image: WritableImage) {

    }

    companion object {
        /**
         * check if net loaded in directory 'resources/CNN_split/CNN_traced' is same net as expectedNet
         *
         * @param expectedNet: path to expectedNet loaded in directory
         */
        fun checkLoaded(expectedNet: String) {
            File("./src/main/resources/CNN_split/CNN_traced/.Metadata.log").
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