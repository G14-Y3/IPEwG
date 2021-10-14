import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.stage.FileChooser
import javafx.stage.StageStyle
import tornadofx.*


class GUI : View("IPEwG") {
    var imageView: ImageView by singleAssign()
    private val testImageUrl = resources.url("test_image.png")

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
                                    val returnCode =
                                        controller.show(imageUrlField.text, gui::imageView.invoke())
                                    if (returnCode == 0) close()
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

    /* Show the image with 'path' (can be path [String] or URL) onto 'imageView'.
    * return 0 if success, -1 if failed. */
    fun show(path: Any, imageView: ImageView): Int {
        return try {
            imageView.image = ImageController().load(path.toString()).get(raw = true)
            //println(path.toString())
            0
        } catch (e: IllegalArgumentException) {
            return try {
                imageView.image =
                    ImageController().load(resources.url(path.toString())).get(raw = true)
                //println(resources.url(path.toString()))
                0
            } catch (e: IllegalArgumentException) {
                alert(
                    type = Alert.AlertType.ERROR,
                    header = "Invalid image path",
                    content = "The image path you entered is incorrect.\n" +
                            "Please check!"
                )
                -1
            }
        }
    }
}
