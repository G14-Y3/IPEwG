package view

import controller.EngineController
import controller.FileController
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.control.TabPane
import javafx.scene.text.FontWeight
import models.EngineModel
import processing.BlurType
import tornadofx.*
import view.component.*
import view.fragment.TransformationList

class FilterPanel : View() {

    private val engine: EngineModel by inject()
    private val engineController: EngineController by inject()
    private val fileController: FileController by inject()

    private val blurList = BlurType.values().toList()

    override val root = tabpane {
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        tab("Filters") {
            vbox {
                splitpane(
                    Orientation.VERTICAL,
                    tabpane {
                        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                        side = Side.LEFT
                        tab<BasicFilterTab>()
                        tab<StyleTransferTab>()
                        tab<ColorAdjustTab>()
                        tab<BlurFilterTab>()

                        tab("frequency transfer") {
                            button("transfer").setOnAction {
                                engineController.frequencyTransfer()
                            }
                        }
                    },
                    TransformationList().root
                ) {
                    setDividerPosition(0, 0.4)
                }
            }
        }

        tab("Steganography")
        {
            vbox {
                splitpane {
                    prefHeight = 1000.0
                    tabpane {
                        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                        side = Side.LEFT
                        tab<EncodeImageTab>()
                        tab<EncodeTextTab>()

                    }

                    vbox {
                        alignment = Pos.CENTER
                        label("Result/Target Image") {
                            vboxConstraints {
                                margin = Insets(5.0, 20.0, 5.0, 10.0)
                            }
                            style {
                                fontWeight = FontWeight.BOLD
                                fontSize =
                                    Dimension(20.0, Dimension.LinearUnits.px)
                            }
                        }
                        imageview(engine.decodeImage) {
                            isPreserveRatio = true
                            fitWidth = 300.0
                            fitHeight = 300.0
                            vboxConstraints {
                                margin = Insets(20.0)
                            }
                        }
                    }

                    orientation = Orientation.VERTICAL
                    setDividerPosition(0, 0.4)
                }
            }
        }

        tab<BatchProcessTab>()
    }
}
