package view.component

import javafx.geometry.Insets
import javafx.scene.layout.VBox
import javafx.scene.text.FontWeight
import tornadofx.*

class BasicFilterTab(basicFilterButtonList: Map<String, () -> Unit>): VBox() {
    init {
        label("Basic Actions") {
            vboxConstraints {
                margin = Insets(20.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }

        hbox {
            padding = Insets(20.0, 20.0, 10.0, 10.0)
            buttonbar {
                basicFilterButtonList.map { (s, callback) ->
                    button(s) {
                        /* The buttons need enough width to load up all labels
                         in them, or the border will change when tabs clicked. */
                        prefWidth = 60.0
                    }.setOnAction { callback() }
                }
            }
        }
    }
}