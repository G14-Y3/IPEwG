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
class CNNVisualization(val netName: String): ImageProcessing {

    @Transient
    val net: Array<Module> = loadNet()


    override fun process(image: WritableImage) {

    }

    fun loadNet(): Array<Module> {
        val fileNames = mutableListOf<String>()
        File(netName).walk().forEach {
            fileNames.add(it.toString())
        }
        fileNames.sort()
        println(fileNames)
        return arrayOf()
    }

}