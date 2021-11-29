package models

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import tornadofx.*
import java.io.File

class BlenderModel : ViewModel() {
    private val engine: EngineModel by inject()

    val blendImageProperty = SimpleObjectProperty<Image>(null)
    private var blendImage by blendImageProperty

    fun loadBlendImage(file: File) {
        blendImage = Image(file.toURI().toString())
    }



}