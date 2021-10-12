import tornadofx.*

class GUI: View() {
    private val testImageUrl = resources.url("test_image.png").toURI()

    override val root = borderpane {
        center {
            hbox {
                stackpane {
                    imageview(ImageController().load(testImageUrl).get(raw = true))
                }
            }
        }
    }
}
