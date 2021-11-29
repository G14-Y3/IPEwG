package view.tab

import controller.EngineController
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.control.Alert
import javafx.scene.control.ComboBox
import javafx.scene.text.FontWeight
import javafx.stage.FileChooser
import models.BlenderModel
import models.EngineModel
import processing.filters.BlendType
import tornadofx.*
import view.CssStyle.Companion.h1
import view.ImagePanel
import view.fragment.TransformationList

class Blend : Fragment("Blend") {

    private val blenderModel: BlenderModel by inject()
    private lateinit var blendTypePicker: ComboBox<BlendType>


    override val root = splitpane {
        orientation = Orientation.HORIZONTAL
        splitpane {
            orientation = Orientation.VERTICAL
            vbox {
                padding = Insets(20.0)
                spacing = 5.0
                label("Blend") { addClass(h1) }
                imageview(blenderModel.blendImageProperty) {
                    isPreserveRatio = true
                    fitHeight = 150.0
                }
                button("Import image to blend") {
                    action { importBlendImage() }
                }

                separator()

                hbox {
                    padding = Insets(5.0)
                    spacing = 5.0

                    text("Select your desired blend algorithm: ")
                    blendTypePicker =
                        combobox(values = BlendType.values().toList()) {
                            selectionModel.selectFirst()
                        }

                }
                hbox {
                    padding = Insets(5.0)
                    spacing = 5.0

                    text("Click to apply")
                    button("Blend") {
                        action { blenderModel.applyBlend(blendTypePicker.value) }
                    }
                }
            }
            add(TransformationList())
        }
        add(ImagePanel())
    }


    private fun importBlendImage() {
        val extensionFilter = FileChooser.ExtensionFilter(
            "PNG, JPEG, JPG, BMP files",
            "*.png", "*.bmp", "*.jpeg", "*.jpg"
        )


        try {
            val fileSelectorTitle = "Import image"
            val fileSelectorMode = FileChooserMode.Single

            val file = chooseFile(
                title = fileSelectorTitle,
                filters = arrayOf(extensionFilter),
                mode = fileSelectorMode
            )
            if (file.isNotEmpty()) {
                blenderModel.loadBlendImage(file.first())
            }

        } catch (e: IllegalArgumentException) {
            alert(
                type = Alert.AlertType.ERROR,
                header = "Invalid image path",
                content = "The image path you entered is incorrect.\n" +
                        "Please check!" + e.toString()
            )
        }
    }
}