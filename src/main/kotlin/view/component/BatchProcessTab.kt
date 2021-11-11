package view.component

import controller.FileController
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.text.FontWeight
import javafx.stage.FileChooser
import models.BatchProcessorModel
import models.EngineModel
import tornadofx.*

class BatchProcessTab : View("Batch") {

    private val batchProcessor: BatchProcessorModel by inject()
    private val engine: EngineModel by inject()

    private val fileController: FileController by inject()

    override val root =
        borderpane {
            top = buttonbar {
                button("Choose images").setOnAction { importImages() }
                button("Apply").setOnAction { batchProcessor.apply() }
                button("Export").setOnAction { exportImages() }
            }
            center = listview(batchProcessor.transformedImages) {
                cellCache {
                    imageview(it) {
                        fitWidth = 100.0
                        fitHeight = 100.0
                        isPreserveRatio = true
                    }
                }

            }


            right = vbox {
                alignment = Pos.CENTER
                label("Transformations") {
                    vboxConstraints {
                        margin = Insets(10.0, 20.0, 10.0, 10.0)
                    }
                    style {
                        fontWeight = FontWeight.BOLD
                        fontSize =
                            Dimension(20.0, Dimension.LinearUnits.px)
                    }
                }

                hbox {
                    alignment = Pos.CENTER
                    padding = Insets(0.0, 10.0, 0.0, 10.0)

                    listview(engine.transformations) {
                        prefWidth = 400.0
                        selectionModel.selectedIndexProperty()
                            .onChange {
                                engine.setCurrentIndex(it)
                            }
                        engine.updateListSelection =
                            { selectionModel.select(engine.currIndex) }
                    }
                }
                hbox {
                    alignment = Pos.CENTER
                    buttonbar {
                        padding = Insets(20.0, 10.0, 20.0, 10.0)
                        button("Undo").setOnAction { fileController.undo() }
                        button("Redo").setOnAction { fileController.redo() }
                        button("Revert").setOnAction { fileController.revert() }
                    }
                }
            }
        }


    private fun importImages() {
        val fileChooser = FileChooser.ExtensionFilter(
            "JPEG, PNG, BMP",
            "*.jpeg",
            "*.jpg",
            "*.png",
            "*.bmp"
        )
        val files = chooseFile(
            title = "Choose images to be processed",
            filters = arrayOf(fileChooser),
            mode = FileChooserMode.Multi
        )

        files.forEach { batchProcessor.loadImage(it) }
    }

    private fun exportImages() {
        val dir = chooseDirectory(
            title = "Output folder"
        )

        if (dir != null) {
            batchProcessor.exportImages(dir)
        }
    }

}