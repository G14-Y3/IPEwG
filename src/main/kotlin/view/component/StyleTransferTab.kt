package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.image.Image
import javafx.scene.text.FontWeight
import processing.styletransfer.NeuralStyles
import tornadofx.*
import java.awt.Desktop
import java.net.URL

class StyleTransferTab : Fragment("Style Transfer") {

    private val engineController: EngineController by inject()

    private val styleTransferMap = listOf(mapOf(
        "Van_Gogh" to NeuralStyles.VAN_GOGH,
        "Picasso" to NeuralStyles.PICASSO,
        "Autumn" to NeuralStyles.AUTUMN
    ), mapOf(
        "Google" to NeuralStyles.GOOGLE,
        "Japan" to NeuralStyles.UKIYOE,
        "Abstract" to NeuralStyles.ABSTRACT
    ))

    override val root = vbox {
        label("Neural Style Transfer") {
            vboxConstraints {
                margin = Insets(20.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }

        hbox {
            textflow {
                padding = Insets(10.0)
                text("Neural style transfer will adopt the appearance/visual " +
                        "style of six pre-defined style images to the input image. We used the " +
                        "idea of fast neural transfer proposed in "
                )
                hyperlink("this paper") {
                    action {
                        Desktop.getDesktop().browse(URL("https://arxiv.org/abs/1508.06576").toURI());
                    }
                }
                text(" to implement the transformation and style network and " +
                        "trained on dix different images. Click the buttons below to visualize the output of each transformation")
            }
        }

        styleTransferMap.map { item ->
            hbox {
                item.map { (str, enum) ->
                    button(str) {
                        prefWidth = 120.0
                        val filename = str.lowercase()
                        imageview(Image("./style_transfer_model/$filename.jpg")) {
                            fitWidth = 30.0
                            fitHeight = 30.0
                        }
                        hboxConstraints {
                            margin = Insets(10.0)
                        }
                        action {
                            engineController.styleTransfer(enum)
                        }
                    }
                }
            }
        }
    }
}