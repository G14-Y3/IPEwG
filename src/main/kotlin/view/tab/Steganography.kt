package view.tab

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.control.Alert
import javafx.scene.control.TabPane
import javafx.scene.text.FontWeight
import javafx.stage.FileChooser
import models.SteganographyModel
import tornadofx.*
import view.component.EncodeImageTab
import view.component.EncodeTextTab
import java.io.File

class Steganography : Fragment("Steganography") {

    private val steganography: SteganographyModel by inject()

    override val root = splitpane {
        orientation = Orientation.HORIZONTAL
        tabpane {
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            side = Side.LEFT
            tab<EncodeImageTab>()
            tab<EncodeTextTab>()
        }

        splitpane {
            orientation = Orientation.VERTICAL

            borderpane {
                top = vbox {
                    padding = Insets(10.0)
                    label("Input Image") {
                        style {
                            fontWeight = FontWeight.BOLD
                            fontSize = Dimension(16.0, Dimension.LinearUnits.px)
                        }
                    }
                }

                center = imageview(steganography.mainImageProperty) {
                    isPreserveRatio = true
                    fitWidth = 200.0
                    fitHeight = 200.0
                }

                bottom = hbox {
                    spacing = 5.0
                    padding = Insets(10.0)
                    alignment = Pos.BASELINE_RIGHT

                    button("Import") {
                        action { importSourceImage() }
                    }

                    button("Use current output image") {
                        action {
                            steganography.useTargetAsSource()
                        }
                    }

                }
            }

            borderpane {
                top = vbox {
                    padding = Insets(10.0)
                    label("Output Image") {
                        style {
                            fontWeight = FontWeight.BOLD
                            fontSize = Dimension(16.0, Dimension.LinearUnits.px)
                        }
                    }
                }

                center = imageview(steganography.targetImageProperty) {
                    isPreserveRatio = true
                    fitWidth = 200.0
                    fitHeight = 200.0
                }

                bottom = hbox {
                    spacing = 5.0
                    padding = Insets(10.0)
                    alignment = Pos.BASELINE_RIGHT

                    button("Export") {
                        action { exportSourceImage() }
                        disableProperty().bind(steganography.targetImageProperty.isNull)
                    }
                }

            }
        }

    }

    private fun chooseSingleFile(import: Boolean = true): File? {
        val extensionFilter = FileChooser.ExtensionFilter(
            "PNG, JPEG, JPG, BMP files",
            "*.png", "*.bmp", "*.jpeg", "*.jpg"
        )

        try {
            val fileSelectorTitle =
                if (import) "Import image" else "Export image"
            val fileSelectorMode =
                if (import) FileChooserMode.Single else FileChooserMode.Save

            val file = chooseFile(
                title = fileSelectorTitle,
                filters = arrayOf(extensionFilter),
                mode = fileSelectorMode
            )
            return file.firstOrNull()

        } catch (e: IllegalArgumentException) {
            alert(
                type = Alert.AlertType.ERROR,
                header = "Invalid image path",
                content = "The image path you entered is incorrect.\n" +
                        "Please check!" + e.toString()
            )
        }
        return null
    }

    private fun exportSourceImage() {
        val file = chooseSingleFile(import = false)
        if (file != null) {
            steganography.exportMainImage(file)
        }
    }

    private fun importSourceImage() {
        val file = chooseSingleFile(import = true)
        if (file != null) {
            steganography.importMainImage(file)
        }
    }

}