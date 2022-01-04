package view.component

import controller.EngineController
import controller.FileController
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.layout.VBox
import javafx.scene.text.FontWeight
import models.EngineModel
import processing.steganography.SteganographyDecoder
import processing.steganography.WaterMarkingTechnique
import tornadofx.*

class WaterMarkTab: Fragment("Water Mark") {
    private val fileController: FileController by inject()
    private val engine: EngineModel by inject()
    private val engineController: EngineController by inject()

    override val root: Parent = hbox {
        vbox {
            label("Add Watermark") {
                vboxConstraints {
                    margin = Insets(20.0, 20.0, 0.0, 10.0)
                }
                style {
                    fontWeight = FontWeight.BOLD
                    fontSize = Dimension(20.0, Dimension.LinearUnits.px)
                }
            }

            hbox {
                padding = Insets(10.0)
                textflow {
                    text("Watermark is a transparent pattern that can be added on to the image. You can upload " +
                            "another image and then choose among three different blending methods below. Click on each of" +
                            "them to view the effect.")
                }
            }

            hbox {
                padding = Insets(10.0)
                textflow {
                    text("You can  ")
                    button("Upload") {
                        action {
                            Utils.imageOperation(mode = "encode", fileController)
                        }
                    }
                    text("  an image and use it as a watermark to add on the input image.")
                }
            }

            hbox {
                imageview(engine.encodeImage) {
                    isPreserveRatio = true
                    fitWidth = 200.0
                    fitHeight = 200.0
                    hboxConstraints {
                        margin = Insets(20.0)
                    }
                }
                vbox {
                    buttonbar {
                        vboxConstraints {
                            marginTop = 20.0
                        }
                        button("By LSB") {
                            action {
                                engineController.waterMark(engine.encodeImage.value, 0, 0, WaterMarkingTechnique.LSB)
                            }
                        }
                        button("By Overlay") {
                            action {
                                engineController.waterMark(engine.encodeImage.value, 0, 0, WaterMarkingTechnique.OVERLAY)
                            }
                        }
                        button("By Multiply") {
                            action {
                                engineController.waterMark(engine.encodeImage.value, 0, 0, WaterMarkingTechnique.MULTIPLY)
                            }
                        }
                    }
                }
            }
        }
    }
}