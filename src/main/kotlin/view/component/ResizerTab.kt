package view.component

import controller.EngineController
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Insets
import javafx.scene.text.FontWeight
import processing.resample.ResampleMethod
import tornadofx.*

class ResizerTab : Fragment("Resize Image") {

    private val engineController: EngineController by inject()

    override val root = vbox {
        val model = DimensionModel(
            ImgSize(
                engineController.previewWidth.toInt(),
                engineController.previewHeight.toInt(),
            )
        )

        label("Resize Image") {
            vboxConstraints {
                margin = Insets(20.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }

        padding = Insets(20.0, 20.0, 10.0, 10.0)

        val resizerList = ResampleMethod.values().toList()
        val comboBox = combobox(values = resizerList)
        comboBox.value = resizerList[0]

        form {
            fieldset("Dimensions") {
                field("width") {
                    textfield(model.width).required()
                }
                field("height") {
                    textfield(model.height).required()
                }
                button("Resize") {
                    enableWhen(model.valid)
                    action {
                        engineController.resample(
                            model.width.value,
                            model.height.value,
                            comboBox.value,
                        )
                    }
                }
            }
        }
    }
}

class ImgSize(width: Int, height: Int) {
    val widthProperty = SimpleIntegerProperty(this, "width", width)
    var name by widthProperty

    val heightProperty = SimpleIntegerProperty(this, "height", height)
    var height by heightProperty
}

class DimensionModel(dimension: ImgSize) : ItemViewModel<ImgSize>(dimension) {
    val width = bind(ImgSize::widthProperty) as IntegerProperty
    val height = bind(ImgSize::heightProperty) as IntegerProperty
}