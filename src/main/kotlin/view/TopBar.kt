package view

import controller.FileController
import javafx.event.EventHandler
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Menu
import javafx.stage.FileChooser
import tornadofx.*
import view.CssStyle.Companion.topbarButton
import java.io.File


class TopBar : View() {
    private val fileController: FileController by inject()

    override val root = menubar {
        menu("Import") {
             onShown = EventHandler {
                 (it.source as Menu).hide()
                 fileOperation(mode = "import image")
             }

            item("")
        }

        menu("Export") {
            onShown = EventHandler {
                (it.source as Menu).hide()
                fileOperation(mode = "export image")
            }

            item("")
        }

        menu("Import Transformation") {
            onShown = EventHandler {
                (it.source as Menu).hide()
                fileOperation(mode = "import JSON")
            }

            item("")
        }

        menu("Export Transformation") {
            onShown = EventHandler {
                (it.source as Menu).hide()
                fileOperation(mode = "export JSON")
            }

            item("")
        }

        menu("Quit") {
            onShown = EventHandler {
                (it.source as Menu).hide()
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

            item("")
        }

//        button("Import Image") {
//            addClass(topbarButton)
//            action {
//                fileOperation(mode = "import image")
//            }
//        }
//        button("Export Image") {
//            addClass(topbarButton)
//            action {
//                fileOperation(mode = "export image")
//            }
//        }
//        button("Import Transformation") {
//            addClass(topbarButton)
//            action {
//                fileOperation(mode = "import JSON")
//            }
//        }
//        button("Export Transformation") {
//            addClass(topbarButton)
//            action {
//                fileOperation(mode = "export JSON")
//            }
//        }
//        button("Quit") {
//            addClass(topbarButton)
//            action {
//                val quitText = "Quit"
//                val result = alert(
//                    type = Alert.AlertType.CONFIRMATION,
//                    header = "Confirm Quit",
//                    content = "Are you sure you want to quit?",
//                    ButtonType.CANCEL,
//                    ButtonType(quitText, ButtonBar.ButtonData.OK_DONE),
//                ).result
//                if (result.text == quitText) {
//                    close()
//                }
//            }
//        }
    }

    private fun fileOperation(mode: String) {

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

        val jsonFilter = arrayOf(
            FileChooser.ExtensionFilter(
                "JSON files (*.json)",
                "*.json"
            )
        )

        try {
            when (mode) {
                "import image" -> {
                    fileSelectorTitle = "Import image"
                    fileSelectorFilter = importFilter
                    fileSelectorMode = FileChooserMode.Single
                }
                "export image" -> {
                    fileSelectorTitle = "Export image"
                    fileSelectorFilter = exportFilter
                    fileSelectorMode = FileChooserMode.Save
                }

                "import JSON" -> {
                    fileSelectorTitle = "import JSON"
                    fileSelectorFilter = jsonFilter
                    fileSelectorMode = FileChooserMode.Single
                }
                "export JSON" -> {
                    fileSelectorTitle = "Export JSON"
                    fileSelectorFilter = jsonFilter
                    fileSelectorMode = FileChooserMode.Save
                }
            }

            val dir = chooseFile(
                title = fileSelectorTitle,
                filters = fileSelectorFilter,
                mode = fileSelectorMode
            ) {
                initialDirectory = File(File("").canonicalPath)
                initialFileName = if (mode.endsWith("JSON")) "transformations.json" else "image.png"
            }
            if (dir.isNotEmpty())
                when (mode) {
                    "import image" ->
                        fileController.loadImage("file:///" + dir[0].toString())
                    "export image" ->
                        fileController.saveImage(
                            dir[0].toString(),
                            dir[0].toString().substring(
                                dir[0].toString().lastIndexOf(".") + 1,
                                dir[0].toString().length
                            )
                        )

                    "import JSON" -> fileController.loadJson(dir[0].toString())
                    "export JSON" -> fileController.saveJson(dir[0].toString())

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