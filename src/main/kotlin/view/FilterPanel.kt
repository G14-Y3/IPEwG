package view

import controller.ImageController
import javafx.geometry.Insets
import javafx.scene.text.FontWeight
import tornadofx.*

class FilterPanel : View() {
    val basicFilterButtonList = listOf("Greyscale", "Inverse Color", "Mirror")
    val basicFilterSliderList = listOf("R", "G", "B", "Brightness", "Contrast", "Saturation")
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
            basicFilterButtonList.map { s -> hbox {
                addClass(CssStyle.buttonBox)
                button(s) {
                    action {
                        imageController.applyFilter()
                    }
                }
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
                    addClass(CssStyle.labelTag)
                }
                slider {

                }
                addClass(CssStyle.filterSlider)
            }}
        }
    }
}