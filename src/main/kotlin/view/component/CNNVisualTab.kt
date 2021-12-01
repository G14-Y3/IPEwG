package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.control.ComboBox
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

    /**
     * Load net from given path,
     * Call python script to load into project directory,
     * Update netBox containing network's structure
     */
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
            netBox.clear()
            val metadata = File("./src/main/resources/CNN_split/CNN_traced/.Metadata.log")
                .readLines()
            for (lineIndex in 1 until metadata.size) {
                val tokens = metadata[lineIndex].split('|')
                val moduleDepth = tokens[0].toInt()
                val layerName = tokens[1]

                if (tokens.size == 2) { // module head line
                    netBox.children.add(
                        label(layerName) {
                            vboxConstraints {
                                margin = Insets(10.0, 20.0, 0.0, 20.0 * moduleDepth)
                            }
                        }
                    )
                    continue
                }

                // Convolution layer
                val channelNum: List<Int> = tokens.subList(2, tokens.size).map { x -> x.toInt() }
                val channelBox = hbox{}
                for (dimension in channelNum) {
                    channelBox.children.add(
                        combobox(values = listOf(0 until dimension).flatten()) {
                            value = 0
                        }
                    )
                }

                val applyButton = button("View CNN Output") {
                    action {
                        val dimentions = mutableListOf<Int>()
                        for (dimentionBox in channelBox.children) {
                            dimentions.add((dimentionBox as ComboBox<Int>).value)
                        }
                        engineController.CNNVisualize(path, lineIndex - 1, dimentions.toList())
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