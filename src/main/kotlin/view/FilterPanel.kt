package view

import javafx.geometry.*
import javafx.scene.control.TabPane
import javafx.scene.text.FontWeight
import models.BatchProcessorModel
import models.EngineModel
import tornadofx.*
import view.component.*
import view.fragment.TransformationList
import view.tab.*

class FilterPanel : View() {

    private val engine: EngineModel by inject()
    private val batchModel: BatchProcessorModel by inject()

    override val root = tabpane {
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        tab<Basic>()
        tab<HistogramEqualisation>()
        tab<Steganography>()
        tab<Watermark>()
        tab<Blend>()
        tab<Batch>()
    }
}
