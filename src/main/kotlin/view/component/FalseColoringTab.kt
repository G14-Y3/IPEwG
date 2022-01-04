package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.text.FontWeight
import processing.filters.FalseColoringMethod
import tornadofx.*

class FalseColoringTab: Fragment("False Coloring") {

    private val engineController: EngineController by inject()

    override val root: Parent = vbox {
        label("False Coloring") {
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
                text("False colouring uses a colour set different from the natural rendition of the " +
                        "image to re-colour the image in order to enhance the contrast between components in the image. " +
                        "It is used for better information visualization or geographical map. You can choose different " +
                        "methods of false colouring. See the project report for more details.")
            }
        }

        hbox {
            padding = Insets(10.0)
            textflow {
                text("You can simply choose a false coloring scheme:  ")
            }
            var scheme = FalseColoringMethod.ENHANCEMENT
            val box = combobox(values = FalseColoringMethod.values().toList()) {
                value = scheme
            }
            text("  and then  ")
            button("Apply") {
                action {
                    engineController.falseColoring(box.value)
                }
            }
            text("  it.")
        }
    }

}