package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.chart.AreaChart
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.layout.HBox
import javafx.scene.text.FontWeight
import net.mahdilamb.colormap.reference.cyclic.HSV
import processing.filters.*
import processing.frequency.FreqProcessRange
import tornadofx.*
import view.CssStyle

class HistogramFilterTab : Fragment("Histogram Equalization") {

    private val engineController: EngineController by inject()
//    private var originalCdf: Array<Int>? = null
//    private var resultCdf: Array<Int>? = null
    private var chart: LineChart<Number, Number>? = null

    override val root = vbox {

        label("Histogram Equalization") {
            vboxConstraints {
                margin = Insets(10.0, 20.0, 0.0, 20.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }
        hbox {
            vbox {
                padding = Insets(20.0, 10.0, 20.0, 10.0)
                label("Equalize in color space: ") {
                    minWidth = 200.0
                }
                val colorSpaceBox = combobox(values =
                    RGBType.values().toList() +
                    HSVType.values().toList() +
                    LabColor.values().toList() +
                    listOf(GrayScaleColorSpace()
                ))

                button("Apply") {
                    action {
                        if (colorSpaceBox.value == null) {
                            alert(
                                type = Alert.AlertType.ERROR,
                                header = "No Operation Selected",
                                content = "Select one color space to histogram equalize before click 'Adjust'",
                                ButtonType.OK
                            )
                        } else {
                            equalize(colorSpaceBox.value)
                        }
                    }
                }
            }
            chart = linechart(
                "Cumulative Distribution Histogram",
                NumberAxis(),
                NumberAxis()
            )
        }
    }

    fun equalize(space: ColorSpace) {
        val hist = HistogramEqualization(space)
        engineController.histogramEqualization(hist)
        val originalCdf = hist.getOriginalCdf()
        val resultCdf = hist.getResultCdf()
        val originalCdfSeries: XYChart.Series<Number, Number> =
            XYChart.Series()
        val resultCdfSeries: XYChart.Series<Number, Number> =
            XYChart.Series()
        for (i in 0 until space.range) {
            originalCdfSeries.data.add(
                XYChart.Data(
                    i,
                    originalCdf[i]
                )
            )
            resultCdfSeries.data.add(
                XYChart.Data(
                    i,
                    resultCdf[i]
                )
            )
        }
        originalCdfSeries.name = "Before Equalization"
        resultCdfSeries.name = "After Equalization"
        chart!!.data.clear()
        chart!!.data.addAll(originalCdfSeries)
        chart!!.data.addAll(resultCdfSeries)
        chart!!.createSymbols = false;
    }
}