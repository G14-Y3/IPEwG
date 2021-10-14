package view

import controller.ImageController
import javafx.geometry.Insets
import javafx.scene.image.ImageView
import models.IPEwGImage
import models.ImageModel
import tornadofx.*

class ImagePanel : View() {
    private val controller: ImageController by inject()
    var showRaw = true

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