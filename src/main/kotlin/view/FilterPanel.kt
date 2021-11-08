package view

import controller.EngineController
import controller.FileController
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.control.TabPane
import javafx.scene.text.FontWeight
import models.EngineModel
import processing.filters.BlurType
import processing.filters.HSVType
import processing.filters.RGBType
import tornadofx.*
import view.component.BasicFilterTab
import view.component.BlurFilterTab
import view.component.ColorAdjustTab
import view.component.StyleTransferTab

class FilterPanel : View() {

    private val engine: EngineModel by inject()
    private val engineController: EngineController by inject()
    private val fileController: FileController by inject()

    private val basicFilterButtonList = mapOf(
        "Inverse Color" to engineController::inverseColour,
        "Greyscale" to engineController::grayscale,
        "Flip Horizontal" to engineController::flipHorizontal,
        "Flip Vertical" to engineController::flipVertical,
        "Edge Detection" to engineController::edgeDetection,
        "Sharpen" to engineController::sharpen,
        "Histogram Equalization" to engineController::histogramEqualization
    )

    private val colorAdjustmentSliderList = mapOf(
        "R" to { factor: Double -> engineController.rgbFilter(factor, RGBType.R) },
        "G" to { factor: Double -> engineController.rgbFilter(factor, RGBType.G) },
        "B" to { factor: Double -> engineController.rgbFilter(factor, RGBType.B) },
        "H" to { factor: Double -> engineController.hsvFilter(factor, HSVType.H) },
        "S" to { factor: Double -> engineController.hsvFilter(factor, HSVType.S) },
        "V" to { factor: Double -> engineController.hsvFilter(factor, HSVType.V) },
    )

    private val blurList = BlurType.values().toList()

    override val root = vbox {
        splitpane {
            tabpane {
                tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                side = Side.LEFT
                tab("Basic Actions") {
                    content = BasicFilterTab(basicFilterButtonList)
                }

                tab("Style Transfer") {
                    content = StyleTransferTab(engineController)
                }
                tab("Color Adjust") {
                    content = ColorAdjustTab(colorAdjustmentSliderList, engineController)
                }
                tab("frequency transfer") {
                    button("transfer").setOnAction {
                        engineController.frequencyTransfer()
                    }
                }
                tab("Blur") {
                    content = BlurFilterTab(engineController)
                }
            }

            vbox {
                alignment = Pos.CENTER
                label("Transformations") {
                    vboxConstraints {
                        margin = Insets(10.0, 20.0, 10.0, 10.0)
                    }
                    style {
                        fontWeight = FontWeight.BOLD
                        fontSize = Dimension(20.0, Dimension.LinearUnits.px)
                    }
                }

                hbox {
                    alignment = Pos.CENTER
                    padding = Insets(0.0, 10.0, 0.0, 10.0)

                    listview(engine.transformations) {
                        prefWidth = 400.0
                        selectionModel.selectedIndexProperty().onChange {
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
            orientation = Orientation.VERTICAL
            setDividerPosition(0, 0.4)
        }
    }
}
