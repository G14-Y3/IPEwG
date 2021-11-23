package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.layout.HBox
import javafx.scene.text.FontWeight
import models.EngineModel
import processing.depthestimation.DepthColorMap
import processing.depthestimation.DepthEstimationModel
import tornadofx.*

class DepthEstimationTab(engineController: EngineController, engine: EngineModel): HBox() {
    private lateinit var modelType: DepthEstimationModel
    private lateinit var colormap: DepthColorMap

    init {
        hbox {
            vbox {
                label("Depth Estimation") {
                    vboxConstraints {
                        margin = Insets(20.0, 20.0, 10.0, 10.0)
                    }
                    style {
                        fontWeight = FontWeight.BOLD
                        fontSize = Dimension(20.0, Dimension.LinearUnits.px)
                    }
                }
                hbox {
                    vbox {
                        hbox {
                            vboxConstraints {
                                margin = Insets(20.0, 20.0, 10.0, 10.0)
                            }
                            label("Estimating the depth using ") {
                                hboxConstraints {
                                    marginTop = 5.0
                                    marginBottom = 20.0
                                }
                            }
                            combobox(values = DepthEstimationModel.values().toList()) {
                                valueProperty()
                                    .addListener(ChangeListener { _, _, new ->
                                        modelType = new
                                    })

                                value = DepthEstimationModel.NYU
                            }
                        }
                        hbox {
                            vboxConstraints {
                                margin = Insets(0.0, 20.0, 10.0, 10.0)
                            }
                            label("Using ") {
                                hboxConstraints {
                                    marginTop = 5.0
                                    marginBottom = 20.0
                                }
                            }
                            combobox(values = DepthColorMap.values().toList()) {
                                valueProperty()
                                    .addListener(ChangeListener { _, _, new ->
                                        colormap = new
                                    })

                                value = DepthColorMap.Viridis
                            }
                            label(" as the depth color map") {
                                hboxConstraints {
                                    marginTop = 5.0
                                    marginBottom = 20.0
                                }
                            }
                        }

                        button("Estimate Depth") {
                            action {
                                engineController.depthEstimation(modelType, colormap)
                            }
                            vboxConstraints {
                                margin = Insets(0.0, 20.0, 10.0, 10.0)
                            }
                        }
                    }
                }
            }
            imageview(engine.depthImage) {
                isPreserveRatio = true
                fitWidth = 200.0
                fitHeight = 200.0
                hboxConstraints {
                    margin = Insets(20.0)
                }
            }
        }
    }
}