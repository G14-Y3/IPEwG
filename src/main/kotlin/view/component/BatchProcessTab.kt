package view.component

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.SelectionMode
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.FileChooser
import models.BatchProcessorModel
import tornadofx.*
import view.fragment.TransformationList

class BatchProcessTab : View("Batch") {

    private val batchProcessor: BatchProcessorModel by inject()

    private val focusedImageProperty = SimpleObjectProperty<Image>(null)
    private var focusedImage by focusedImageProperty

    private val imagesListView = listview(batchProcessor.transformedImages) {
        selectionModel.selectionMode = SelectionMode.SINGLE
        padding = Insets(10.0)
        cellCache { image ->
            hbox {
                alignment = Pos.CENTER
                imageview(image) {
                    fitWidth = 100.0
                    fitHeight = 100.0
                    isPreserveRatio = true
                }
                text("${image.width.toInt()} x ${image.height.toInt()}") {
                    fill = Color.GRAY
                    font = Font(10.0)
                }
                spacing = 5.0
            }
        }

        onUserDelete { batchProcessor.remove(indexInParent) }
    }

    override val root = borderpane {
        padding = Insets(10.0)
        left = borderpane {

            center = imagesListView

            bottom = buttonbar {
                button("Import").action { importImages() }
                button("Apply").action { batchProcessor.apply() }
                button("Remove").action {
                    batchProcessor.remove(
                        *imagesListView.selectionModel.selectedIndices.toIntArray()
                    )
                }
                button("Preview") {
                    action {
                        focusedImage = imagesListView.selectedItem
                    }
                }
                button("Revert").action { batchProcessor.revert() }
                button("Export").action { exportImages() }
                padding = Insets(15.0)
            }
        }

        right = splitpane(
            Orientation.VERTICAL,
            vbox {
                padding = Insets(5.0)
                alignment = Pos.CENTER
                imageview(focusedImageProperty) {
                    fitWidth = 400.0
                    fitHeight = 250.0
                    isPreserveRatio = true
                }
            },
            find<TransformationList>().root
        )
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