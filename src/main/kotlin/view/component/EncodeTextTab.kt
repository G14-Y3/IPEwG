package view.component

import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.text.FontWeight
import javafx.stage.FileChooser
import models.SteganographyModel
import tornadofx.*

class EncodeTextTab : Fragment("Encode/Decode Text") {

    private var key = ""
    private var onlyRChannel = false

    private lateinit var inputTextArea: TextArea
    private lateinit var bitPicker: ComboBox<Int>

    private val steganographyModel: SteganographyModel by inject()

    override val root = vbox {
        label("Encode/Decode Text") {
            vboxConstraints {
                margin = Insets(10.0, 20.0, 0.0, 20.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }
        vbox {
            padding = Insets(10.0)
            spacing = 5.0

            label(
                "Supply a text sequence in the text box on the right and encode with the options below"
            ) {
                isWrapText = true
            }
            checkbox("with a random key") {
                isDisable = true
                action {}
            }
            hbox {
                spacing = 5.0
                label("with the  ")
                bitPicker = combobox(values = listOf(1, 2, 3, 4)) {
                    value = 4
                }
                label("  lower bits encoded")
            }
            checkbox("Only use a single(R) channel (Default is to use all three channels)") {
                isDisable = true
                action {
                    onlyRChannel = this.isSelected
                }
            }
            buttonbar {
                vboxConstraints {
                    marginTop = 20.0
                }
                button("Encode") {
                    action {
                        steganographyModel.encodeText(
                            inputTextArea.text,
                            key,
                            bitPicker.value,
                            onlyRChannel
                        )
                    }
                    disableProperty().bind(steganographyModel.mainImageProperty.isNull)
                }

                button("Decode") {
                    action { steganographyModel.decodeText() }
                    disableProperty().bind(steganographyModel.mainImageProperty.isNull)
                }
            }
        }

        vbox {
            spacing = 5.0
            padding = Insets(5.0)

            label("Text to encode")
            inputTextArea = textarea {
                isWrapText = true
                isEditable = true
            }

        }

        vbox {
            spacing = 5.0
            padding = Insets(5.0)

            label("Decoded text")
            textarea {
                isWrapText = true
                isEditable = false
                textProperty().bind(steganographyModel.decodedText)
            }

        }
    }

    private fun importSourceImage() {
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
                steganographyModel.importMainImage(file.first())
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