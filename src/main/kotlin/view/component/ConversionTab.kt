package view.component

import javafx.geometry.Insets
import javafx.scene.layout.VBox
import javafx.scene.text.FontWeight
import tornadofx.*

enum class ConverterTypes { ColorSpace }

class ConversionTab(converterList: Map<ConverterTypes, Map<String, () -> Unit>>) : VBox() {

    init {
        label("Conversions") {
            vboxConstraints {
                margin = Insets(20.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }

        label("Color Space Conversions") {
            vboxConstraints {
                margin = Insets(20.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.SEMI_BOLD
                fontSize = Dimension(16.0, Dimension.LinearUnits.px)
            }
        }

        hbox {
            padding = Insets(.0, 20.0, .0, 10.0)
            buttonbar {
                converterList[ConverterTypes.ColorSpace]!!.map { (s, callback) ->
                    button(s) {
                        /* The buttons need enough width to load up all labels
                         in them, or the border will change when tabs clicked. */
                        prefWidth = 160.0
                    }.setOnAction { callback() }
                }
            }
        }
    }
}
