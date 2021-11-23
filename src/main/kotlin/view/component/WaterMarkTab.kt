package view.component

import controller.EngineController
import controller.FileController
import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.layout.VBox
import javafx.scene.text.FontWeight
import models.EngineModel
import processing.steganography.SteganographyDecoder
import processing.steganography.WaterMarkingTechnique
import tornadofx.*

class WaterMarkTab(fileController: FileController, engine: EngineModel, engineController: EngineController): VBox() {
    init {
        hbox {
            vbox {
                label("Add Watermark") {
                    vboxConstraints {
                        margin = Insets(10.0, 20.0, 0.0, 20.0)
                    }
                    style {
                        fontWeight = FontWeight.BOLD
                        fontSize = Dimension(20.0, Dimension.LinearUnits.px)
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
                        hbox {
                            button("Upload") {
                                action {
                                    Utils.imageOperation(mode = "encode", fileController)
                                }
                            }
                            label("  an image and add water mark by adjusting the image below") {
                                hboxConstraints {
                                    marginTop = 5.0
                                    marginBottom = 20.0
                                }
                            }
                        }
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
}