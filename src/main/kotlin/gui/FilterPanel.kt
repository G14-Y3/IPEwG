package gui

import javafx.geometry.Insets
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.stage.StageStyle
import tornadofx.*

class FilterPanel : View() {
    override val root = vbox {
        label("Basic Filters") {
            vboxConstraints {
                margin = Insets(20.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }
        hbox {
            button("Greyscale") {
                hboxConstraints {
                    margin = Insets(10.0)
                }
            }
            button("Inverse Color") {
                hboxConstraints {
                    margin = Insets(10.0)
                }
            }
            button("filter1") {
                hboxConstraints {
                    margin = Insets(10.0)
                }
            }
        }

        hbox {
            button("filter1") {
                hboxConstraints {
                    margin = Insets(0.0, 10.0, 10.0, 10.0)
                }
            }
            button("filter1") {
                hboxConstraints {
                    margin = Insets(0.0, 10.0, 10.0, 10.0)
                }
            }
            button("filter1") {
                hboxConstraints {
                    margin = Insets(0.0, 10.0, 10.0, 10.0)
                }
            }
            button("filter1") {
                hboxConstraints {
                    margin = Insets(0.0, 10.0, 10.0, 10.0)
                }
            }
            button("filter1") {
                hboxConstraints {
                    margin = Insets(0.0, 10.0, 10.0, 10.0)
                }
            }
        }
    }
}