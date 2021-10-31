import controller.FileController
import javafx.scene.control.Alert
import javafx.stage.FileChooser
import tornadofx.FileChooserMode
import tornadofx.alert
import tornadofx.chooseFile
import java.io.File

class Utils {
    companion object {
         fun imageOperation(mode: String, controller: FileController) {

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
                    "import", "encode" -> {
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
                            controller.load("file:///" + dir[0].toString())
                        "encode" ->
                            controller.loadEncodeImage("file:///" + dir[0].toString())
                        "export" ->
                            controller.save(
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
}