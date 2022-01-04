package view.component

import controller.EngineController
import controller.FileController
import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.layout.VBox
import javafx.scene.text.FontWeight
import javafx.stage.FileChooser
import models.EngineModel
import processing.filters.BlendType
import tornadofx.*
import java.io.File

class BlendTab : Fragment("Blend") {

    private val engine: EngineModel by inject()
    private val engineController: EngineController by inject()
    private val fileController: FileController by inject()

    override val root = vbox {
        label("Blend") {
            vboxConstraints {
                margin = Insets(20.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }

        hbox {
            hbox {
                padding = Insets(10.0)
                textflow {
                    text(
                        "Blending will mix two images using different blending methods, e.g. " +
                                "multiply, overlay, dissolve. The resulting effect will look like two images stack on top " +
                                "of one another in a special way. Some blending effect might produce water-mark style effects." +
                                "You can first  "
                    )
                    button("import").setOnAction { importBlendImage() }
                    text("  a second image and then choose the blending method  ")
                    val blendList = BlendType.values().toList()
                    val comboBox = combobox(values = blendList)
                    comboBox.value = blendList[0]
                    text("  to  ")
                    button("blend").setOnAction {
                        engineController.blend(comboBox.value)
                    }
                    text("  the two images together.")
                }
            }

            vbox {
                padding = Insets(20.0, 10.0, 20.0, 10.0)

                label("Blend Image")

                val blendView = imageview(engine.blendImage) {
                    isPreserveRatio = true
                    prefWidth = 200.0
                    prefHeight = 200.0
                }
                blendView.fitHeight = 250.0
            }
        }
    }

    private fun importBlendImage() {
        val importFilter = arrayOf(
            FileChooser.ExtensionFilter(
                "PNG files (*.png)",
                "*.png"
            ),
            FileChooser.ExtensionFilter(
                "Bitmap files (*.bmp)",
                "*.bmp"
            ),
            FileChooser.ExtensionFilter(
                "JPEG files (*.jpeg, *.jpg)",
                "*.jpeg",
                "*.jpg"
            )
        )

        try {
            val fileSelectorTitle = "Import image"
            val fileSelectorMode = FileChooserMode.Single

            val dir = chooseFile(
                title = fileSelectorTitle,
                filters = importFilter,
                mode = fileSelectorMode
            ) {
                initialDirectory = File(File("").canonicalPath)
                initialFileName = "IPEwG_result_image"
            }
            if (dir.isNotEmpty()) {
                fileController.loadBlendImage("file:///" + dir[0].toString())
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