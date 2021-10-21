package view

import controller.FileController
import javafx.scene.control.Alert
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
                    imageOperation(mode = "import")
                }
            }
            item("_Export...") {
                action {
                    imageOperation(mode = "export")
                }
            }
            item("_Quit") {
                action {
                    val result = alert(
                        type = Alert.AlertType.CONFIRMATION,
                        header = "Confirm Quit",
                        content = "Are you sure you want to quit?"
                    ).result
                    if (result == ButtonType.OK) close()
                }
            }
        }
//        menu("_View") {
//            item("_Basic Actions") {
//                action {
//
//                }
//            }
//            item("_RGB")
//            item("_HSV")
//        }
        menu("_Help") {
            item("_How to")
        }
    }

    private fun imageOperation(mode: String) {

        var fileSelectorTitle = ""
        var fileSelectorFilter = emptyArray<FileChooser.ExtensionFilter>()
        var fileSelectorMode = FileChooserMode.None

        val importFilter = arrayOf(
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

        val exportFilter = arrayOf(
            importFilter[0], importFilter[1],
            FileChooser.ExtensionFilter(
                "JPEG files (*.jpg)",
                "*.jpg"
            )
        )

        try {
            when (mode) {
                "import" -> {
                    fileSelectorTitle = "Import image"
                    fileSelectorFilter = importFilter
                    fileSelectorMode = FileChooserMode.Single
                }
                "export" -> {
                    fileSelectorTitle = "Export image"
                    fileSelectorFilter = exportFilter
                    fileSelectorMode = FileChooserMode.Save
                }
            }
            val dir = chooseFile(
                title = fileSelectorTitle,
                filters = fileSelectorFilter,
                mode = fileSelectorMode
            ) {
                initialDirectory = File(File("").canonicalPath)
                initialFileName = "IPEwG_result_image"
            }
            if (dir.isNotEmpty())
                when (mode) {
                    "import" ->
                        fileController.load("file:///" + dir[0].toString())
                    "export" ->
                        fileController.save(
                            dir[0].toString(),
                            dir[0].toString().substring(
                                dir[0].toString().lastIndexOf(".") + 1,
                                dir[0].toString().length
                            )
                        )
                }

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