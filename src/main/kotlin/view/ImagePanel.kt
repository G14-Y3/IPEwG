package view

import controller.ImageController
import javafx.geometry.Insets
import javafx.scene.image.ImageView
import models.ImageModel
import tornadofx.*

var oriImageView: ImageView by singleAssign()

class ImagePanel : View() {
    val uri: String by param()
    private val testImageUrl = resources.url(uri).toURI()

    override val root = vbox {
        // TODO: refactor this code
        imageview(ImageModel(uri).load(testImageUrl).get(raw = true)) {
            oriImageView = this
            vboxConstraints {
                margin = Insets(20.0)
            }
        }
    }
}