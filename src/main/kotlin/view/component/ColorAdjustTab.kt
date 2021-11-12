package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.control.Slider
import javafx.scene.text.FontWeight
import processing.filters.HSVType
import processing.filters.RGBType
import tornadofx.*

class ColorAdjustTab : View("Colour Adjustment") {

    private val engineController: EngineController by inject()

    private val colorAdjustmentSliderList = mapOf(
        "R" to { factor: Double ->
            engineController.rgbFilter(
                factor,
                RGBType.R
            )
        },
        "G" to { factor: Double ->
            engineController.rgbFilter(
                factor,
                RGBType.G
            )
        },
        "B" to { factor: Double ->
            engineController.rgbFilter(
                factor,
                RGBType.B
            )
        },
        "H" to { factor: Double ->
            engineController.hsvFilter(
                factor,
                HSVType.H
            )
        },
        "S" to { factor: Double ->
            engineController.hsvFilter(
                factor,
                HSVType.S
            )
        },
        "V" to { factor: Double ->
            engineController.hsvFilter(
                factor,
                HSVType.V
            )
        },
    )

    override val root = vbox {
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