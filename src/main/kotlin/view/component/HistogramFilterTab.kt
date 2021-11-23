package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.chart.AreaChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.layout.HBox
import javafx.scene.text.FontWeight
import processing.filters.HistogramEqualization
import tornadofx.*

class HistogramFilterTab : Fragment("Histogram Equalization") {

    private val engineController: EngineController by inject()

    override val root = hbox {
        var originalCdf: Array<Int>
        var resultCdf: Array<Int>
        var chart: AreaChart<Number, Number>? = null

        hbox {
            vbox {
                label("Histogram Equalization") {
                    vboxConstraints {
                        margin = Insets(10.0, 20.0, 0.0, 20.0)
                    }
                    style {
                        fontWeight = FontWeight.BOLD
                        fontSize = Dimension(20.0, Dimension.LinearUnits.px)
                    }
                }
                button("Equalize (Using grayscale values)") {
                    vboxConstraints {
                        margin = Insets(10.0, 20.0, 0.0, 20.0)
                    }
                    action {
                        val hist = HistogramEqualization()
                        engineController.histogramEqualization(hist)
                        originalCdf = hist.getOriginalCdf()
                        resultCdf = hist.getResultCdf()
                        val originalCdfSeries: XYChart.Series<Number, Number> =
                            XYChart.Series()
                        val resultCdfSeries: XYChart.Series<Number, Number> =
                            XYChart.Series()
                        for (i in 0..255) {
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
                        chart!!.data.addAll(originalCdfSeries)
                        chart!!.data.addAll(resultCdfSeries)
                    }
                }
            }
            chart = areachart(
                "Cumulative Distribution Histogram",
                NumberAxis(),
                NumberAxis()
            )
        }
    }
}