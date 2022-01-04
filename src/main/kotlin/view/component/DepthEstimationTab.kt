package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.layout.HBox
import javafx.scene.text.FontWeight
import models.EngineModel
import processing.depthestimation.DepthColorMap
import processing.depthestimation.DepthEstimationModel
import tornadofx.*

class DepthEstimationTab : Fragment("Depth Estimation") {
    private val engineController: EngineController by inject()
    private val engine: EngineModel by inject()

    private lateinit var modelType: DepthEstimationModel
    private lateinit var colormap: DepthColorMap

    override val root: Parent = hbox {
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
                padding = Insets(10.0)
                textflow {
                    text("Depth estimation will create a depth map of the image with the colour of the" +
                            " depth map being the relative estimated depth. You can choose two different machine learning" +
                            " models and three different color map below. Click Estimate depth button below to visualize.")
                }
            }

            hbox {
                padding = Insets(10.0)
                textflow {
                    text("Estimating the depth using the  ")
                    combobox(values = DepthEstimationModel.values().toList()) {
                        valueProperty()
                            .addListener(ChangeListener { _, _, new ->
                                modelType = new
                            })

                        value = DepthEstimationModel.NYU
                    }
                    text("  model and  ")
                    combobox(values = DepthColorMap.values().toList()) {
                        valueProperty()
                            .addListener(ChangeListener { _, _, new ->
                                colormap = new
                            })

                        value = DepthColorMap.Viridis
                    }
                    text("  as the colour map.")
                }
            }

            hbox {
                padding = Insets(10.0)
                button("Estimate Depth") {
                    action {
                        engineController.depthEstimation(modelType, colormap, engine.previewImage.value.width / 2, engine.previewImage.value.height / 2)
                    }
                    vboxConstraints {
                        margin = Insets(0.0, 20.0, 10.0, 10.0)
                    }
                }
            }
        }
        // TODO: the code below is used to display the original image but currently we just commented it out
//        imageview(engine.depthImage) {
//            isPreserveRatio = true
//            fitWidth = 200.0
//            fitHeight = 200.0
//            hboxConstraints {
//                margin = Insets(20.0)
//            }
//        }
    }
}