package view

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.text.FontWeight
import processing.RGBType
import tornadofx.*

class FilterPanel : View() {

    private val engineController: EngineController by inject()

    private val basicFilterButtonList = mapOf(
        "Inverse Color" to engineController::inverseColour,
        "Greyscale" to engineController::grayscale,
    )

    private val basicFilterSliderList = mapOf(
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
    )

    override val root = vbox {
        label("Quick Action") {
            vboxConstraints {
                margin = Insets(20.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }

        hbox {
            padding = Insets(0.0, 10.0, 0.0, 10.0)
            basicFilterButtonList.map { (s, callback) ->
                hbox {
                    addClass(CssStyle.checkBox)
                    checkbox(s) {
                        action { callback() }
                    }
                }
            }
        }

        label("Quick Action") {
            vboxConstraints {
                margin = Insets(20.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }

        vbox {
            vboxConstraints {
                margin = Insets(10.0)
            }

            basicFilterSliderList.map { (label, op) ->
                hbox {
                    label(label) {
                        addClass(CssStyle.labelTag)
                    }
                    val slider = slider {
                        min = 0.0
                        max = 100.0
                    }
                    slider.value = 100.0
                    slider.valueChangingProperty()
                        .addListener(ChangeListener { _, _, _ -> op(slider.value / 100) })

                    addClass(CssStyle.filterSlider)
                }
            }
        }
    }
}