package view

import controller.ImageController
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.text.FontWeight
import models.*
import tornadofx.*

class FilterPanel : View() {
    val basicFilterButtonList = mapOf(
        "Inverse Color" to InverseColour(),
        "Greyscale" to GreyScale(),
        "Mirror" to Mirror()
    )
    val basicFilterSliderList = mapOf(
        "R" to RGB(RGB_type.R),
        "G" to RGB(RGB_type.G),
        "B" to RGB(RGB_type.B),
        "Brightness" to Brightness(),
        "Contrast" to Contrast(),
        "Saturation" to Satuation()
    )
    val imageController: ImageController by inject()

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
            basicFilterButtonList.map { (s, op) ->
                hbox {
                    addClass(CssStyle.checkBox)
                    checkbox(s) {
                        action {
                            imageController.applyFilter(op)
                        }
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
                    slider.valueChangingProperty().addListener(object : ChangeListener<Boolean?> {
                        override fun changed(
                            source: ObservableValue<out Boolean?>?,
                            oldValue: Boolean?,
                            newValue: Boolean?
                        ) {
                            println()
                            op.setSlidingVal(slider.value / 100.0)
                            imageController.applyFilter(op)
                        }
                    })
                    addClass(CssStyle.filterSlider)
                }
            }
        }
    }
}