package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.control.ComboBox
import javafx.scene.text.FontWeight
import processing.filters.BlurType
import tornadofx.*

class BlurFilterTab : Fragment("Blur") {
    private val engineController: EngineController by inject()

    override val root = vbox {
        label("Blur") {
            vboxConstraints {
                margin = Insets(20.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }

        var comboBox: ComboBox<BlurType>? = null
        val blurList = BlurType.values().toList()

        hbox {
            padding = Insets(10.0)
            textflow {
                text("Blur is achieved by convolving the image with a blurring kernel. The kernel " +
                        "is an n x n matrix with the size n varying from 0 (no blur effect at all) to 10. You can " +
                        "choose a blurring method with a kernel size to visualize the blurring effect. A blurring method " +
                        "is just a different way of blurring. Use the  ")
                comboBox = combobox(values = blurList) {
                    prefWidth = 150.0
                }
                text("  method to blur the image and choose a kernel size n, then click Adjust to apply the blur to the image.")
            }
        }

        comboBox!!.value = blurList[0]

        val slider = SliderWithSpinner(0.0, 10.0, ChangeListener { _, _, new ->
            engineController.blur(
                new.toDouble(),
                comboBox!!.value
            )
        }).withLabel("Kernel Size")

        comboBox!!.valueProperty()
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