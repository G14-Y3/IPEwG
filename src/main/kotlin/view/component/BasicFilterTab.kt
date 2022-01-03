package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.control.Tooltip
import javafx.scene.text.FontWeight
import tornadofx.*

class BasicFilterTab : Fragment("Basic Actions") {

    private val engineController: EngineController by inject()

    private val basicFilterButtonList = listOf(mapOf(
        "Inverse Color" to engineController::inverseColour,
        "Greyscale" to engineController::grayscale,
        "Flip Horizontal" to engineController::flipHorizontal,
        "Flip Vertical" to engineController::flipVertical
    ), mapOf(
        "Edge Detection" to engineController::edgeDetection,
        "Sharpen" to engineController::sharpen
    ))

    private val basicFilterTooltipText = mapOf(
        "Inverse Color" to "Inverse all the pixels in the image",
        "Greyscale" to "Turn every pixel into grayscaled colour",
        "Flip Horizontal" to "Turn every pixel into either black or white based on its grayscale value",
        "Flip Vertical" to "Flip the image vertically",
        "Edge Detection" to "Flip the image horizontally",
        "Sharpen" to "Enhancing the edge contrast of the image to increase its visual sharpness"
    )

    override val root = vbox {
        label("Basic Actions") {
            vboxConstraints {
                margin = Insets(20.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }

        basicFilterButtonList.map { item ->
            hbox {
                padding = Insets(10.0, 20.0, 0.0, 0.0)
                buttonbar {
                    item.map { (s, callback) ->
                        button(s) {
                            prefWidth = 120.0
                            action {
                                callback.call()
                            }
                            tooltip = Tooltip(basicFilterTooltipText[s])
                        }
                    }
                }
            }
        }

        label("Contrast") {
            vboxConstraints {
                margin = Insets(10.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }

        vbox {
            val slider = SliderWithSpinner(0.0, 1.0, ChangeListener { _, _, new ->
                engineController.contrast(new as Double)
            }).withLabel("Factor")
            this.children.add(slider.build())
            button("Apply Contrast") {
                tooltip = Tooltip("Adjust the contrast of the image")
                vboxConstraints {
                    margin = Insets(10.0, 20.0, 10.0, 10.0)
                }
                action {
                    engineController.submitAdjustment()
                }
            }
        }

        label("Rotation") {
            vboxConstraints {
                margin = Insets(10.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }

        vbox {
            val slider = SliderWithSpinner(0.0, 360.0, ChangeListener { _, _, new ->
                engineController.rotate(new as Double)
            }).withLabel("Rotate Degree")
            this.children.add(slider.build())
            button("Apply Rotation") {
                tooltip = Tooltip("Rotate the image clockwise, ranging from 0 to 360 degree")
                vboxConstraints {
                    margin = Insets(10.0, 20.0, 10.0, 10.0)
                }
                action {
                    engineController.submitAdjustment()
                }
            }
        }
    }
}