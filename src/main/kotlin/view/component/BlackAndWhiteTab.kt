package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.text.FontWeight
import tornadofx.*

class BlackAndWhiteTab: Fragment("Black & White") {

    private val engineController: EngineController by inject()

    override val root: Parent = vbox {
        label("Black and White") {
            vboxConstraints {
                margin = Insets(20.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }

        val slider = SliderWithSpinner(0.0, 255.0, ChangeListener { _, _, new ->
            engineController.blackAndWhite(new as Double)
        }).withLabel("Threshold")

        this.children.add(slider.build())

        button("Apply") {
            vboxConstraints {
                margin = Insets(20.0, 20.0, 10.0, 10.0)
            }

            action {
                engineController.submitAdjustment()
            }
        }
    }
}