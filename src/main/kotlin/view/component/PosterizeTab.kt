package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.text.FontWeight
import tornadofx.*

class PosterizeTab : Fragment("Posterize") {
    private val engineController: EngineController by inject()

    override val root = vbox {
        label("Posterize") {
            vboxConstraints {
                margin = Insets(20.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }

        val slider = SliderWithSpinner(0.0, 50.0, ChangeListener { _, _, new ->
            engineController.posterize(new.toDouble())
        })

        this.children.add(
            slider.build()
        )

        slider.getSlider().valueProperty()
            .addListener { _, _, newVal -> slider.getSlider().value = newVal.toInt().toDouble() }

        buttonbar {
            padding = Insets(20.0, 10.0, 20.0, 10.0)
            button("Adjust").setOnAction {
                engineController.submitAdjustment()
                slider.getSlider().value = 0.0
            }
            button("Reset").setOnAction {
                engineController.resetAdjustment()
                slider.getSlider().value = 0.0
            }
        }
    }
}