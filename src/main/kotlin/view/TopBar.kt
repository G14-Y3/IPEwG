package view

import controller.ImageController
import javafx.scene.control.Alert
import javafx.stage.FileChooser
import models.IPEwGImage
import models.ImageModel
import tornadofx.*


class TopBar : View() {
    val controller: ImageController by inject()

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
            controller.load(if (dir.toString() == "[]") "" else "file:///" + dir[0].toString())
        } catch (e: IllegalArgumentException) {
            alert(
                type = Alert.AlertType.ERROR,
                header = "Invalid image path",
                content = "The image path you entered is incorrect.\n" +
                        "Please check!" + e.toString()
            )
        }
    }
}