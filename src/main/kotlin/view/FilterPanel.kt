package view

import javafx.geometry.*
import javafx.scene.control.TabPane
import javafx.scene.text.FontWeight
import models.BatchProcessorModel
import models.EngineModel
import tornadofx.*
import view.component.*
import view.component.TransformationList

class FilterPanel : View() {

    private val engine: EngineModel by inject()
    private val batchModel: BatchProcessorModel by inject()

    override val root = tabpane {
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        tab("Spatial Domain") {
            vbox {
                splitpane(
                    Orientation.VERTICAL,
                    tabpane {
                        side = Side.LEFT
                        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                        tab<BasicFilterTab>()
                        tab<ColorAdjustTab>()
                        tab<ConversionTab>()
                        tab<SaltPepperTab>()
                    },
                    TransformationList().root
                ) {
                    setDividerPosition(0, 0.55)
                }
            }
        }

        tab("Advanced Spatial Domain") {
            vbox {
                splitpane(
                    Orientation.VERTICAL,
                    tabpane {
                        side = Side.LEFT
                        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                        tab<ResizerTab>()
                        tab<BlendTab>()
                        tab<WaterMarkTab>()
                        tab<FalseColoringTab>()
                        tab<HistogramFilterTab>()
                        tab<PosterizationTab>()
                    },
                    TransformationList().root
                ) {
                    setDividerPosition(0, 0.55)
                }
            }
        }

        tab("Frequency Domain") {
            vbox {
                splitpane(
                    Orientation.VERTICAL,
                    tabpane {
                        side = Side.LEFT
                        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                        tab<BlurFilterTab>()
                        tab<FrequencyTab>()
                    },
                    TransformationList().root
                ) {
                    setDividerPosition(0, 0.55)
                }
            }
        }

        tab("Neural Network Based") {
            vbox {
                splitpane(
                    Orientation.VERTICAL,
                    tabpane {
                        side = Side.LEFT
                        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                        tab<StyleTransferTab>()
                        tab<DepthEstimationTab>()
                        tab<DenoiseTab>()
                        tab<CNNVisualTab>()
                    },
                    TransformationList().root
                ) {
                    setDividerPosition(0, 0.55)
                }
            }
        }

        tab("Steganography") {
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

        tab<BatchProcessTab>() {
            batchModel.isBatchTabOpened.bind(selectedProperty().not())
        }
    }
}
