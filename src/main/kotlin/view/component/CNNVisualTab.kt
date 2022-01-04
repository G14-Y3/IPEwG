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

    private val scrollNet = scrollpane {  }
    private val netBox = vbox { }

    private var outputHeight = 0
    private var outputWidth = 0

    private val shapeBox = textfield("1x3x512x512")

    override val root = vbox {
        padding = Insets(20.0, 10.0, 20.0, 10.0)

        label("CNN Visualize") {
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }

        hbox {
            padding = Insets(10.0)
            textflow {
                text(
                    "CNN visualization can display the selected CNN layer's output in a grayscale image.\n\n" +
                            "To use the tool: \n1. specify the input tensor shape of the network: "
                )
                this.children.add(shapeBox)
                text("\n2. ")
                button("Import CNN") {
                    action {
                        try {
                            val dir = chooseFile(
                                title = "Import pytorch CNN",
                                filters = arrayOf(
                                    FileChooser.ExtensionFilter(
                                        "pythorch file",
                                        "*.pt", "*.log"
                                    )
                                ),
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
                text(
                    "that will be investigated. The structure of the network shall be displayed below.\n" +
                            "3.Select the channel you wish to visualize and click \"View output tensor\" after each layer to see the image"
                )
            }

        }
        scrollNet.content = netBox
        this.children.add(scrollNet)
    }

    /**
     * Load net from given path,
     * Call python script to load into project directory,
     * Update netBox containing network's structure
     */
    private fun importNet(path: String) {
        val shape = observableListOf<Int>()
        shapeBox.characters.toString().split('x').forEach { s -> shape.add(s.toInt()) }

        val exitCode = CNNVisualization.loadNet(path, shape)
        if (exitCode != 0)
            alert(
                type = Alert.AlertType.ERROR,
                header = "Load torch network failed",
                content = "Please check if input tensor shape, file path and network structure are valid"
            )

        netBox.clear()
        val metadata = File("./src/main/resources/CNN_split/CNN_traced/.Metadata.log")
            .readLines()
        var nonLayer = 1
        for (lineIndex in 1 until metadata.size) {
            val tokens = metadata[lineIndex].split('|')
            val moduleDepth = tokens[0].toInt()
            val layerName = tokens[1]
            val layerNum = lineIndex - nonLayer

            // module head line
            if (tokens.size == 2) {
                netBox.children.add(
                    hbox {
                        padding = Insets(.0, .0, .0, 20.0 * moduleDepth)
                        label(layerName)
                    }
                )
                nonLayer += 1
                continue
            }

            // Convolution layer
            val channelNum: List<Int> = tokens.subList(2, tokens.size - 2).map { x -> x.toInt() }
            val channelBox = hbox {}
            for (dimension in channelNum) {
                channelBox.children.add(
                    combobox(values = listOf(0 until dimension).flatten()) {
                        value = 0
                    }
                )
            }
            outputHeight = tokens[tokens.size - 2].toInt()
            outputWidth = tokens[tokens.size - 1].toInt()

            val applyButton = button("View Output Tensor($outputHeight x $outputWidth)") {
                action {
                    val dimentions = mutableListOf<Int>()
                    for (dimentionBox in channelBox.children) {
                        dimentions.add((dimentionBox as ComboBox<Int>).value)
                    }
                    engineController.CNNVisualize(path, shape, layerNum, lineIndex, dimentions.toList())
                }
                isVisible = false
            }
            netBox.children.add(
                hbox {
                    padding = Insets(.0, .0, .0, 20.0 * moduleDepth)
                    label(layerName)
                    this.children.add(channelBox)
                    this.children.add(applyButton)

                    this.onHover { applyButton.isVisible = it }
                }
            )
        }
    }

}
