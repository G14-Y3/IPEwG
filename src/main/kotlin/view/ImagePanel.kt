package view

import controller.ImageController
import javafx.geometry.Insets
import tornadofx.View
import tornadofx.imageview
import tornadofx.vbox
import tornadofx.vboxConstraints

class ImagePanel : View() {
    private val controller: ImageController by inject()

    override val root = vbox {
        imageview(controller.activeImage.image) {
            vboxConstraints {
                margin = Insets(20.0)
            }

            setOnMouseClicked {
                controller.toggleActiveImage()
            }
        }
    }
}