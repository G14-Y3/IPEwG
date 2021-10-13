import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.stage.FileChooser
import javafx.stage.StageStyle
import tornadofx.*

var oriImageView: ImageView by singleAssign()

class GUI : View("IPEwG") {

    private val testImageUrl = resources.url("test_image.png").toURI()

    override val root = borderpane {
        center {
            vbox {
                stackpane {
                    //oriImage
                    imageview(ImageController().load(testImageUrl).get(raw = true)) {
                        oriImageView = this
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
                                        controller.show(imageUrlField.text)
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
    fun show(imageUrl: String) {
        //if (imageUrl != "") println(imageUrl)
        oriImageView.image = ImageController().loadByPath(imageUrl).get(raw = true)
    }
}
