package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.layout.VBox
import javafx.scene.text.FontWeight
import tornadofx.*

enum class ConverterTypes { ColorSpace }

class ConversionTab : Fragment("Color Space Conversions") {

    private val engineController: EngineController by inject()
    
    private val converterList = mapOf(
        ConverterTypes.ColorSpace to mapOf(
            "sRGB to Linear RGB" to engineController::convertsRGBToLinearRGB,
            "Linear RGB to sRGB" to engineController::convertLinearRGBTosRGB,
        )
    )

    override val root = vbox {
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
