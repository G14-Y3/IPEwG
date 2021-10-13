import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.control.Alert
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.input.ScrollEvent
import javafx.scene.input.ZoomEvent
import javafx.stage.FileChooser
import javafx.stage.StageStyle
import tornadofx.*


var oriImageView: ImageView by singleAssign()
// double value that define the image width, changes as zoom event happens
var zoomedWidth: DoubleProperty = SimpleDoubleProperty(WINDOW_WIDTH)
// window x and y value, defining the left up corner position of the image
var windowX: DoubleProperty = SimpleDoubleProperty(0.0)
var windowY: DoubleProperty = SimpleDoubleProperty(0.0)
var image_h_w_ratio: Double = 0.0

class GUI : View("IPEwG") {

    private val testImageUrl = resources.url("test_image.png").toURI()

    override val root = borderpane {
        center {
            vbox {
                // todo: zoom in with the mouse position as the zoom center
                scrollpane {
                    // listen to scroll property, change the window x y value
                    this.addEventFilter(ScrollEvent.ANY) {

                        // calculating the view box's left top coordinate's x, y values' upperbound
                        var xBound = oriImageView.fitWidth - WINDOW_WIDTH
                        var yBound = 0.0
                        if (oriImageView.image.height > WINDOW_HEIGHT) {
                            yBound = oriImageView.fitHeight - WINDOW_HEIGHT
                        }

                        windowX.set(cast(0.0, xBound, windowX.get() - it.deltaX))
//                        windowY.set(cast(0.0, yBound, windowY.get() - it.deltaY))

//                        println("x = " + windowX.get())
//                        println("y = " + windowY.get())

                    }
                    // listen to zoomProperty to detect zoom in & out action
                    this.addEventFilter(ZoomEvent.ANY) {
                        var ratio = 1.0
                        if (it.zoomFactor > 1) {
                            ratio = 1.01
                        } else if (it.zoomFactor < 1) {
                            // only zoom out when image is smaller than original image
                            if (zoomedWidth.get() > WINDOW_WIDTH ) {
                                ratio = 1 / 1.01
                            }
                        }
                        // update image size and left top coordinate according to the ratio
                        zoomedWidth.set(zoomedWidth.get() * ratio)
                        oriImageView.fitWidth = zoomedWidth.get()

                        println("x = " + oriImageView.image.width)
                        println("y = " + oriImageView.image.height)
//                        println("scene = " + oriImageView.scaleX)
//                        windowX.set(windowX.get() * ratio)
//                        windowY.set(windowY.get() * ratio)
                        // update view box's left top coordinate
//                        oriImageView.viewport = Rectangle2D(windowX.get(), windowY.get(), WINDOW_WIDTH, WINDOW_HEIGHT)
                    }
                    //oriImage
                    imageview(ImageController().load(testImageUrl).get(raw = true)) {
                        oriImageView = this
                        image_h_w_ratio = oriImageView.y / oriImageView.x
//                        println(oriImageView.y)
//                        println(oriImageView.fitHeight)
//                        println(oriImageView.image.height)
                        oriImageView.fitWidth = zoomedWidth.get()
                        oriImageView.preserveRatioProperty().set(true)
                    }
                    // fixing the window for displaying image
                    // todo: any more elegant way of fixing window size?
                    this.minWidth = WINDOW_WIDTH
                    this.maxWidth = WINDOW_WIDTH
                    this.minHeight = WINDOW_HEIGHT
                    this.maxHeight = WINDOW_HEIGHT
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
                                                    "*.png", "*.bmp", "*.jpeg", "*.jpg"
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
        windowX.set(0.0)
        windowY.set(0.0)
    }
}
