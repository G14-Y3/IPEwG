package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.layout.VBox
import javafx.scene.text.FontWeight
import processing.BlurType
import tornadofx.*

class BlurFilterTab(engineController: EngineController): VBox() {
    init {
        label("Blur") {
            vboxConstraints {
                margin = Insets(20.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }

        val blurList = BlurType.values().toList()
        val comboBox = combobox(values = blurList)
        comboBox.value = blurList[0]

        val slider = SliderWithSpinner(0.0, 10.0, ChangeListener { _, _, new ->
            engineController.blur(
                new.toDouble(),
                comboBox.value
            )
        }).withComboBox(comboBox)

        comboBox.valueProperty()
            .addListener(ChangeListener { _, _, _ ->
                engineController.resetAdjustment()
                slider.getSlider().value = 0.0
            })

        this.children.add(
            slider.build()
        )

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