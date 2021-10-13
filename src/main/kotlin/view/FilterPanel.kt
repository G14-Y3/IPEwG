package view

import javafx.geometry.Insets
import javafx.scene.text.FontWeight
import tornadofx.*

class FilterPanel : View() {
    val basicFilterButtonList = listOf("Greyscale", "Inverse Color", "Mirror")
    val basicFilterSliderList = listOf("R", "G", "B", "Brightness", "Contrast", "Saturation")
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
            basicFilterButtonList.map { s -> hbox {
                addClass(IPEwGStyle.buttonBox)
                button(s)
            }}
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

            basicFilterSliderList.map { label -> hbox {
                label(label) {
                    addClass(IPEwGStyle.labelTag)
                }
                slider {

                }
                addClass(IPEwGStyle.filterSlider)
            }}
        }
    }
}