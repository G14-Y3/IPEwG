package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.text.FontWeight
import tornadofx.*

class BasicFilterTab : Fragment("Basic Actions") {

    private val engineController: EngineController by inject()

    private val basicFilterButtonList = mapOf(
        "Inverse Color" to engineController::inverseColour,
        "Greyscale" to engineController::grayscale,
        "Flip Horizontal" to engineController::flipHorizontal,
        "Flip Vertical" to engineController::flipVertical,
        "Edge Detection" to engineController::edgeDetection,
        "Sharpen" to engineController::sharpen,
        "Histogram Equalization" to engineController::histogramEqualization
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

        hbox {
            padding = Insets(20.0, 20.0, 10.0, 10.0)
            buttonbar {
                basicFilterButtonList.map { (s, callback) ->
                    button(s) {
                        /* The buttons need enough width to load up all labels
                         in them, or the border will change when tabs clicked. */
                        prefWidth = 60.0
                    }.setOnAction { callback.call() }
                }
            }
        }
    }
}