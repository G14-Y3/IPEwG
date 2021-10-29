package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.control.Slider
import javafx.scene.control.Tab
import javafx.scene.layout.VBox
import javafx.scene.text.FontWeight
import tornadofx.*

class ColorAdjustTab(colorAdjustmentSliderList: Map<String, (Double) -> Unit>, engineController: EngineController) : VBox() {
    init {
        label("Color Adjustment") {
            vboxConstraints {
                margin = Insets(20.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }
        vboxConstraints {
            margin = Insets(10.0)
        }
        val sliders = ArrayList<Slider>()
        colorAdjustmentSliderList.map { (label, op) ->
            val slider = SliderWithSpinner(
                minVal = -100.0, maxVal = 100.0,
                op = ChangeListener { _, _, new -> op(new.toDouble() / 100 + 1) }
            ).withLabel(label as String)
            this.children.add(
                slider.build()
            )
            sliders += slider.getSlider()
        }
        buttonbar {
            padding = Insets(20.0, 10.0, 20.0, 10.0)
            button("Adjust").setOnAction {
                engineController.submitAdjustment()
                sliders.forEach { it.value = 0.0 }
            }
            button("Reset").setOnAction {
                engineController.resetAdjustment()
                sliders.forEach { it.value = 0.0 }
            }
        }
    }
}