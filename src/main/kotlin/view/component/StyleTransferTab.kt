package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.image.Image
import javafx.scene.layout.HBox
import processing.styletransfer.NeuralStyles
import tornadofx.*

class StyleTransferTab(engineController: EngineController): HBox() {
    val style_transfer_map = mapOf(
        "Van_Gogh" to NeuralStyles.VAN_GOGH,
        "Picasso" to NeuralStyles.PICASSO,
        "Autumn" to NeuralStyles.AUTUMN,
        "Google" to NeuralStyles.GOOGLE,
        "Japan" to NeuralStyles.UKIYOE,
        "Abstract" to NeuralStyles.ABSTRACT
    )

    init {
        hbox{
            style_transfer_map.map { (str, enum) ->
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
}