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
    private val previewWidth get() = engineController.previewWidth.toInt()
    private val previewHeight get() = engineController.previewHeight.toInt()

    private val widthValidator = mapOf(
        ResampleMethod.PointWithZeros to { width: Int ->
            if (width % previewWidth != 0)
                "Width must be a multiple of source for this method"
            else null
        }
    )

    private val heightValidator = mapOf(
        ResampleMethod.PointWithZeros to { height: Int ->
            if (height % previewHeight != 0)
                "Height must be a multiple of source for this method"
            else null
        }
    )

    override val root = vbox {
        val model = DimensionModel(ImgSize(previewWidth, previewHeight))

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
                    textfield(model.width).validator {
                        if (it.isNullOrBlank()) error("This field is required")
                        else {
                            val message =
                                widthValidator[comboBox.value]?.let { it1 -> it1(it.toInt()) }
                            if (message != null) error(message) else null
                        }
                    }
                }
                field("height") {
                    textfield(model.height).validator {
                        if (it.isNullOrBlank()) error("This field is required")
                        else {
                            val message =
                                heightValidator[comboBox.value]?.let { it1 -> it1(it.toInt()) }
                            if (message != null) error(message) else null
                        }
                    }
                }
                button("Resize") {
                    enableWhen(model.valid)
                    action {
                        engineController.resample(
                            previewWidth,
                            previewHeight,
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