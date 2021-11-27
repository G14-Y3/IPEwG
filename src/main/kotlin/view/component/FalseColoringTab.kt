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
                margin = Insets(10.0, 20.0, 0.0, 20.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }

        hbox {
            var scheme = FalseColoringMethod.ENHANCEMENT
            label("Choose a false coloring scheme ") {
                hboxConstraints {
                    margin = Insets(10.0, 20.0, 0.0, 20.0)
                }
            }

            val box = combobox(values = FalseColoringMethod.values().toList()) {
                value = scheme
            }

            label(" and then ") {
                hboxConstraints {
                    margin = Insets(10.0, 20.0, 0.0, 20.0)
                }
            }

            button("Apply") {
                action {
                    engineController.falseColoring(box.value)
                }
            }
        }
    }

}