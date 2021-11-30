package view.component

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.text.FontWeight
import javafx.stage.FileChooser
import models.SteganographyModel
import tornadofx.*

class EncodeImageTab : Fragment("Encode/Decode Image") {

    private lateinit var bitPicker: ComboBox<Int>
    private lateinit var encodingMethodPicker: ComboBox<String>
    private lateinit var inputKeyArea: TextArea

    private val steganography: SteganographyModel by inject()

    override val root = vbox {
        spacing = 5.0
        padding = Insets(10.0)
        label("Encode/Decode Image") {
            padding = Insets(10.0)
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }

        hbox {
            spacing = 5.0
            button("Upload") {
                action { importHiddenImage() }
            }
            label("an image and encode with the options below") {
                isWrapText = true
            }
        }

        label("using passphrase:")
        inputKeyArea = textarea("RandomKey123") {
            isEditable = true
            prefRowCount = 1
            prefColumnCount = 50
        }

        hbox {
            spacing = 5.0
            label("with the")
            bitPicker = combobox(values = listOf(1, 2, 3, 4)) {
                value = 4
            }
            label("lower bits encoded")
        }

        hbox {
            spacing = 5.0
            label("encode by the")
            encodingMethodPicker =
                combobox(values = listOf("dimension", "pixel order")) {
                    selectionModel.selectFirst()
                }
            label("of the encode image")
        }

        buttonbar {
            padding = Insets(10.0)
            button("Encode") {
                action {
                    steganography.encodeImage(
                        inputKeyArea.text,
                        bitPicker.value,
                        encodingMethodPicker.value == "pixel order"
                    )
                }
                disableProperty().bind(
                    steganography.imageToHide.isNull.or(
                        steganography.mainImageProperty.isNull
                    )
                )
            }
            button("Decode") {
                action { steganography.decodeImage() }
                disableProperty().bind(steganography.mainImageProperty.isNull)
            }
        }


        vbox {
            padding = Insets(10.0)
            label("Image to hide") {
                style {
                    fontWeight = FontWeight.BOLD
                }
            }
            imageview(steganography.imageToHide) {
                alignment = Pos.CENTER
                isPreserveRatio = true
                fitWidth = 200.0
                fitHeight = 300.0
            }
            visibleProperty().bind(steganography.imageToHide.isNotNull)
        }
    }

    private fun importHiddenImage() {
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
                steganography.importEncodeImage(file.first())
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