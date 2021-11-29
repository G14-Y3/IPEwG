package view.tab

import controller.EngineController
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import tornadofx.*
import view.CssStyle.Companion.h1
import view.ImagePanel
import view.component.*
import view.fragment.TransformationList

class Basic : Fragment("Basic") {

    private val engineController: EngineController by inject()
    private lateinit var mainPanel: Node


    private val basicFilterButtonList = mapOf(
        "Inverse Color" to engineController::inverseColour,
        "Greyscale" to engineController::grayscale,
        "Flip Horizontal" to engineController::flipHorizontal,
        "Flip Vertical" to engineController::flipVertical,
        "Edge Detection" to engineController::edgeDetection,
        "Sharpen" to engineController::sharpen
    )

    private val basicFilters = mapOf(
        "Blur" to BlurFilterTab(),
        "RGB-HSV" to ColorAdjustTab(),
        "B&W" to BlackAndWhiteTab(),
        "Style Transfer" to StyleTransferTab(),
        "Frequency Transfer" to FrequencyTab(),
        "Conversion" to ConversionTab(),
        "Salt & Pepper" to SaltPepperTab(),
        "False Colouring" to FalseColoringTab(),
        "De-noise" to DenoiseTab(),
        "Depth Estimation" to DepthEstimationTab(),
    )


    override val root = splitpane {
        orientation = Orientation.HORIZONTAL
        splitpane {
            orientation = Orientation.VERTICAL
            vbox {
                padding = Insets(20.0)

                label("Quick Actions")
                flowpane {
                    for ((s, callback) in basicFilterButtonList)
                        button(s) {
                            action { callback() }
                        }

                    hgap = 10.0
                    vgap = 10.0
                    padding = Insets(15.0)
                }


                label("Transformations")
                flowpane {
                    for ((s, node) in basicFilters)
                        button(s) {
                            action { mainPanel.replaceChildren(node.root) }
                        }

                    hgap = 10.0
                    vgap = 10.0
                    padding = Insets(15.0)
                }

                separator()

                scrollpane {
                    mainPanel = vbox {
                        isFitToWidth = true
                        label("Pick a transformation!")
                    }
                }
                children.asSequence()
                    .filter { it is Label }
                    .forEach { it.addClass(h1) }
            }
            add(TransformationList())

        }
        add(ImagePanel())
    }


}