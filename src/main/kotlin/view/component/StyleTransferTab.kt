package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.image.Image
import processing.styletransfer.NeuralStyles
import tornadofx.*

class StyleTransferTab : Fragment("Style Transfer") {

    private val engineController: EngineController by inject()

    private val styleTransferMap = mapOf(
        "Van_Gogh" to NeuralStyles.VAN_GOGH,
        "Picasso" to NeuralStyles.PICASSO,
        "Autumn" to NeuralStyles.AUTUMN,
        "Google" to NeuralStyles.GOOGLE,
        "Japan" to NeuralStyles.UKIYOE,
        "Abstract" to NeuralStyles.ABSTRACT
    )

    override val root = hbox {
        styleTransferMap.map { (str, enum) ->
            button(str) {
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