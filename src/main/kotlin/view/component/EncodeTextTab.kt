package view.component

import controller.EngineController
import controller.FileController
import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.TextArea
import javafx.scene.text.FontWeight
import models.EngineModel
import processing.steganography.SteganographyDecoder
import tornadofx.*

class EncodeTextTab: Fragment("Encode/Decode Text") {

    private var bits = 4
    private var key = ""
    private var hasUndone = false
    private var onlyRChannel = false
    private var originalText = ""

    private val engine: EngineModel by inject()
    private val engineController: EngineController by inject()
    private val fileController: FileController by inject()

    override val root = hbox {
        var decodeTextarea: TextArea? = null
        hbox {
            vbox {
                label("Encode/Decode Text") {
                    vboxConstraints {
                        margin = Insets(10.0, 20.0, 0.0, 20.0)
                    }
                    style {
                        fontWeight = FontWeight.BOLD
                        fontSize = Dimension(20.0, Dimension.LinearUnits.px)
                    }
                }
                hbox {
                    vbox {
                        hboxConstraints {
                            margin = Insets(20.0)
                        }
                        label("Supply a text sequence in the text box \n " +
                                "on the right and encode with the options below") {
                            hboxConstraints {
                                marginTop = 5.0
                                marginBottom = 20.0
                            }
                        }
                        checkbox("with a random key") {
                            isDisable = true
                            action {

                            }
                        }
                        hbox {
                            label("with the  ") {
                                hboxConstraints {
                                    marginTop = 5.0
                                }
                            }
                            combobox(values = listOf(1, 2, 3, 4)) {
                                valueProperty()
                                    .addListener(ChangeListener { _, _, new ->
                                        bits = new
                                    })

                                value = 4
                            }
                            label("  lower bits encoded") {
                                hboxConstraints {
                                    marginTop = 5.0
                                }
                            }
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
                                    val original_image = engine.originalImage.value

                                    if (originalText.length > original_image.width * original_image.height) {
                                        alert(
                                            type = Alert.AlertType.ERROR,
                                            header = "Could not encode the text",
                                            content = "The text length exceeds the maximum amount of information that the original image can hold",
                                            ButtonType.OK
                                        )
                                    } else {
                                        hasUndone = false
                                        engineController.encodeText(originalText, key, bits, onlyRChannel)
                                    }
                                }
                            }
                            button("Undo") {
                                action {
                                    if (!hasUndone) fileController.undo()
                                    hasUndone = true
                                }
                            }
                            button("Decode") {
                                action {
                                    val decoder = SteganographyDecoder(false)
                                    engine.transform(decoder)
                                    decodeTextarea!!.text = decoder.get_result_text()
                                }
                            }
                        }
                    }
                    decodeTextarea = textarea {
                        prefWidth = 300.0
                        hboxConstraints {
                            hboxConstraints {
                                margin = Insets(10.0)
                            }
                        }
                        isWrapText = true
                        textProperty().addListener(ChangeListener { _, _, newValue ->
                            originalText = newValue
                        })
                    }
                }
            }
        }
    }
}