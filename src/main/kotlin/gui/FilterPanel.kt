package gui

import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.stage.StageStyle
import tornadofx.*

class FilterPanel : View() {
    val filterButtonList = listOf("Greyscale", "Inverse Color", "Mirror")
    val filterSliderList = listOf("R", "G", "B", "Brightness", "Contrast", "Saturation")
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
            filterButtonList.map { s -> hbox {
                addClass(IPEwGStyle.buttonBox)
                button("Greyscale")
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

            filterSliderList.map { label -> hbox {
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