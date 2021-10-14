import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.stage.FileChooser
import javafx.stage.StageStyle
import tornadofx.*



class GUI : View("IPEwG") {
    var imageView: ImageView by singleAssign()
    private val testImageUrl = resources.url("test_image.png").toURI()

    override val root = borderpane {
        center {
            vbox {
                stackpane {
                    //oriImage
                    imageview(ImageController().load(testImageUrl).get(raw = true)) {
                        imageView = this
                    }
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

class ImportImage : Fragment("Import image..") {
    private var imageUrlField: TextField by singleAssign()
    private val controller: GUIController by inject()
    private val gui: GUI by inject()
    override val root = borderpane {
        center {
            vbox {
                stackpane {
                    hbox {
                        form {
                            fieldset {
                                field("Path") {
                                    textfield {
                                        imageUrlField = this
                                    }
                                    button("browse..") {
                                        action {
                                            val filter = arrayOf(
                                                FileChooser.ExtensionFilter(
                                                    "PNG files (*.png)",
                                                    "*.png"
                                                ),
                                                FileChooser.ExtensionFilter(
                                                    "Bitmap files (*.bmp)",
                                                    "*.bmp"
                                                ),
                                                FileChooser.ExtensionFilter(
                                                    "JPEG files (*.jpeg, *.jpg)",
                                                    "*.jpeg",
                                                    "*.jpg"
                                                )
                                            )
                                            val dir = chooseFile(
                                                "Select image",
                                                filters = filter,
                                                mode = FileChooserMode.Single
                                            )
                                            imageUrlField.text =
                                                if (dir.toString() == "[]") "" else dir[0].toString()
                                        }
                                    }
                                }
                            }
                            alignment = Pos.CENTER
                            button("OK") {
                                action {
                                    try {
                                        controller.show(imageUrlField.text, gui::imageView.invoke())
                                        close()
                                    } catch (e: IllegalArgumentException) {
                                        alert(
                                            type = Alert.AlertType.ERROR,
                                            header = "Invalid image path",
                                            content = "The image path you entered is incorrect.\n" +
                                                    "Please check!"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

class GUIController : Controller() {

    /* Show the image with 'imageUrl' onto 'imageView' */
    fun show(imageUrl: String, imageView: ImageView) {
        //if (imageUrl != "") println(imageUrl)
        imageView.image = ImageController().loadByPath(imageUrl).get(raw = true)
    }
}
