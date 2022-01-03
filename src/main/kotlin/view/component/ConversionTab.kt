package view.component

import com.sun.javafx.application.HostServicesDelegate
import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import tornadofx.*
import java.awt.Desktop
import java.net.URL

enum class ConverterTypes { ColorSpace }

class ConversionTab : Fragment("Colour Space Conversions") {

    private val engineController: EngineController by inject()
    
    private val converterList = mapOf(
        ConverterTypes.ColorSpace to mapOf(
            "sRGB to Linear RGB" to engineController::convertsRGBToLinearRGB,
            "Linear RGB to sRGB" to engineController::convertLinearRGBTosRGB,
        )
    )

    override val root = vbox {
        label("Colour Space Conversions") {
            vboxConstraints {
                margin = Insets(20.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }

        hbox {
            padding = Insets(10.0, 20.0, .0, 10.0)
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

        hbox {
            textflow {
                hboxConstraints {
                    margin = Insets(10.0, 20.0, 10.0, 10.0)
                }
                text("Convert colour spaces between CIELab and Linear RGB. For more information about CIELab and, visit ") {
                    textAlignment = TextAlignment.CENTER
                }
                hyperlink("this link") {
                    action {
                        Desktop.getDesktop().browse(URL("https://knowledge.ulprospector.com/10780/pc-the-cielab-lab-system-the-method-to-quantify-colors-of-coatings/").toURI());
                    }
                }
                text(". Colour space conversion is inherently used in other image processing techniques as well, such as resize/rescale and histogram equalization.")
            }
        }
    }
}
