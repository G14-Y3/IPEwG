package view.tab

import javafx.geometry.Orientation
import tornadofx.Fragment
import tornadofx.splitpane
import view.ImagePanel
import view.component.HistogramFilterTab

class HistogramEqualisation :  Fragment("Histogram Equalisation") {

    override val root = splitpane {
        orientation = Orientation.HORIZONTAL
        splitpane {
            orientation = Orientation.VERTICAL
            add(HistogramFilterTab())
        }
        add(ImagePanel())
    }
}