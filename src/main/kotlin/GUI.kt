import javafx.geometry.Pos
import javafx.scene.control.TextField
import javafx.stage.FileChooser
import javafx.stage.StageStyle
import models.*
import tornadofx.*

//lateinit var oriImage: Image

class GUI: View("IPEwG") {

    private val testImageUrl = resources.url("test_image.png").toURI()

    override val root = borderpane {
        center {
            vbox {
                stackpane {
                    //oriImage
                    imageview(ImageController().load(testImageUrl).get(raw = true))
                }
                stackpane {
                    alignment = Pos.CENTER
                    button("Open...") {
                        action {
                            find(ImportImage::class).openModal(stageStyle = StageStyle.UTILITY)
                        }
                    }
                }
            }
        }
    }
}

class ImportImage: Fragment("Import image..") {
    var imageUrlField: TextField by singleAssign()
    val controller: GUIController by inject()
    override val root = borderpane{
        center {
            vbox {
                stackpane {
                    hbox {
                        form {
                            fieldset {
                                field("Path") {
                                    textfield() {
                                        imageUrlField = this
                                    }
                                    button("browse..") {
                                        action {
                                            val filter = arrayOf(FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"),
                                                FileChooser.ExtensionFilter("Bitmap files (*.bmp)", "*.bmp"),
                                                FileChooser.ExtensionFilter("JPEG files (*.jpeg, *.jpg)", "*.jpeg", "*.jpg"))
                                            var dir = chooseFile("Select image", filters = filter, mode = FileChooserMode.Single)
                                            imageUrlField.text = if (dir.toString() == "[]")  "" else dir[0].toString()
                                        }
                                    }
                                }
                            }
                            alignment = Pos.CENTER
                            button("OK") {
                                action {
                                    controller.output(imageUrlField.text)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

class GUIController: Controller() {
    fun output(imageUrl: String) {
        if (imageUrl != "") println(imageUrl)
    }
}
