package view

import controller.FileController
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File


class TopBar : View() {
    private val fileController: FileController by inject()

    override val root = menubar {
        menu("_File") {
            item("_Import...") {
                action {
                    Utils.imageOperation(mode = "import", fileController)
                }
            }
            item("_Export...") {
                action {
                    Utils.imageOperation(mode = "export", fileController)
                }
            }
            item("_Quit") {
                action {
                    val quitText = "Quit"
                    val result = alert(
                        type = Alert.AlertType.CONFIRMATION,
                        header = "Confirm Quit",
                        content = "Are you sure you want to quit?",
                        ButtonType.CANCEL,
                        ButtonType(quitText, ButtonBar.ButtonData.OK_DONE),
                    ).result
                    if (result.text == quitText) {
                        close()
                    }
                }
            }
        }
        menu("View") {
            item("Basic Filters") {
                action {

                }
            }
            item("Steganography") {
                action {

                }
            }
        }
        menu("_Help") {
            item("_How to")
        }
    }
}