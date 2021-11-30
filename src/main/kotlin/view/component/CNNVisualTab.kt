package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.text.FontWeight
import javafx.stage.FileChooser
import org.pytorch.IValue
import org.pytorch.Module
import tornadofx.*
import java.io.File


class CNNVisualTab : View("CNN Visualize") {

    private val engineController: EngineController by inject()

    private val netBox = vbox {}

    override val root = vbox {
        padding = Insets(20.0, 10.0, 20.0, 10.0)

        label("CNN Visualize") {
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }

        button("Import CNN") {
            action {
                try {
                    val dir = chooseFile(
                        title = "Import pytorch CNN",
                        filters = arrayOf(FileChooser.ExtensionFilter(
                            "pythorch file",
                            "*.pt", "*.log"
                        )),
                        mode = FileChooserMode.Single
                    )
                    if (dir.isNotEmpty()) {
                        importNet(dir[0].toString())
                    }
                } catch (e: IllegalArgumentException) {
                    alert(
                        type = Alert.AlertType.ERROR,
                        header = "Invalid pytorch file path",
                        content = "The torch file path you entered is incorrect.\n" +
                                "Please check!" + e.toString()
                    )
                }
            }
        }

        button("apply") {
            action {
                engineController.CNNVisualize("./src/main/resources/CNN_split/CNN_trace")
            }
        }
    }

    private fun importNet(path: String) {
        val process = Runtime.getRuntime().exec("python3 ./src/main/resources/CNN_split/CNN_spliter.py $path")
        val exitCode = process.waitFor()
        if (exitCode != 0) {
            alert(
                type = Alert.AlertType.ERROR,
                header = "Load torch network failed",
                content = "The torch network provided couldn't be imported.\n" +
                        "Please check!" + path
            )
        }


    }
}