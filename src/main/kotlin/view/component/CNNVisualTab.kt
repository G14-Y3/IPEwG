package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.text.FontWeight
import javafx.stage.FileChooser
import processing.filters.CNNVisualization
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
        this.children.add(netBox)
    }

    private fun importNet(path: String) {
        val exitCode = CNNVisualization.loadNet(path)
        if (exitCode != 0) {
            alert(
                type = Alert.AlertType.ERROR,
                header = "Load torch network failed",
                content = "The torch network provided couldn't be imported.\n" +
                        "Please check!" + path
            )
        } else {
            val metadata = File("./src/main/resources/CNN_split/CNN_traced/.Metadata.log")
                .readLines()
            for (lineIndex in 1 until metadata.size) {
                val tokens = metadata[lineIndex].split(' ')
                println(tokens)
                val moduleDepth = tokens[0].toInt()
                val layerName = tokens.subList(1, tokens.size - 1).reduce { x, y -> x + y }
                val channelNum = tokens[tokens.size - 1].toIntOrNull()
                if (channelNum == null) {
                    netBox.children.add(
                        label(layerName) {
                            vboxConstraints {
                                margin = Insets(10.0, 20.0, 0.0, 20.0 * moduleDepth)
                            }
                        }
                    )
                    continue
                }

                val channelBox = combobox(values = listOf(0 until channelNum).flatten())
                val applyButton = button("View CNN Output") {
                    action {
                        engineController.CNNVisualize(path, lineIndex - 1, channelBox.value)
                    }
                    isVisible = false
                }
                netBox.children.add(
                    hbox {
                        label(layerName) {
                            vboxConstraints {
                                margin = Insets(10.0, 20.0, 0.0, 20.0 * moduleDepth)
                            }
                        }
                        this.children.add(channelBox)
                        this.children.add(applyButton)

                        this.onHover { applyButton.isVisible = it }
                    }
                )
            }
//            val layerPaths = CNNVisualization.getLayerPaths()
//            for ((layerIndex, s) in layerPaths.withIndex()) {
//                val relativeLayerPath = s.substringAfter("CNN_traced/")
//                val moduleDepth = relativeLayerPath.filter { it == '/' }.count()
//                val layerName = relativeLayerPath.substring(relativeLayerPath.lastIndexOf('/') + 1)
//                netBox.children.add(
//                    button(layerName) {
//                        vboxConstraints {
//                            margin = Insets(10.0, 20.0, 0.0, 20.0 * moduleDepth)
//                        }
//
//                        action {
//                            engineController.CNNVisualize(path, layerIndex)
//                        }
//                    }
//                )
//            }
        }
    }
}