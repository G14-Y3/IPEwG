package models

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Alert
import javafx.scene.image.Image
import processing.filters.Blend
import processing.filters.BlendType
import tornadofx.ViewModel
import tornadofx.alert
import tornadofx.getValue
import tornadofx.setValue
import java.io.File

class BlenderModel : ViewModel() {
    private val engine: EngineModel by inject()

    val blendImageProperty = SimpleObjectProperty(Image("./test_image.png"))
    private var blendImage by blendImageProperty

    fun loadBlendImage(file: File) {
        blendImage = Image(file.toURI().toString())
    }

    fun applyBlend(type: BlendType) {
        if (blendImage == null) {
            alert(
                type = Alert.AlertType.ERROR,
                header = "Insufficient information",
                content = "Please import image to be blended first"
            )
            return
        }

        engine.transform(Blend(blendImage, type))
    }

}