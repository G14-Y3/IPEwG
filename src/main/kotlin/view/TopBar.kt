package view

import javafx.scene.control.Alert
import javafx.stage.FileChooser
import models.ImageModel
import tornadofx.*

class GUIController : Controller() {
    fun show(imageUrl: String) {
        // TODO: refactor this code
        oriImageView.image = ImageModel(imageUrl).load(imageUrl).get(raw = true)
    }
}

class TopBar : View() {
    private val controller: GUIController by inject()

    override val root = menubar {
        menu("File") {
            item("Import...") {
                action {
                    selectImage()
                }
            }
            item("Export...")
            item("Quit")
        }
        menu("View") {
            item("Basic Action")
            item("Advanced")
            item("Advanced")
        }
        menu("Help") {
            item("How to")
        }
    }

    fun selectImage() {
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

        try {
            controller.show(if (dir.toString() == "[]") "" else dir[0].toString())
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