package gui

import ImageController
import ImportImage
import javafx.scene.image.ImageView
import javafx.stage.StageStyle
import tornadofx.*

var oriImageView: ImageView by singleAssign()

class ImagePanel : View() {
    val uri: String by param()
    private val testImageUrl = resources.url(uri).toURI()

    override val root = vbox {
        imageview(ImageController().load(testImageUrl).get(raw = true)) {
            oriImageView = this
        }

        button("Open...") {
            action {
                find(ImportImage::class).openModal(stageStyle = StageStyle.UTILITY)
            }
        }
    }
}