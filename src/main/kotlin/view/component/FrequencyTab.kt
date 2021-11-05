package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.layout.HBox
import javafx.scene.text.FontWeight
import processing.FreqProcessRange
import processing.FreqProcessType
import processing.frequency.ButterWorthFilter
import processing.frequency.GaussianFilter
import processing.frequency.IdleFreqFilter
import tornadofx.*

class FrequencyTab(
    private val engineController: EngineController) : HBox() {

    init {
        label("frequency filter type: ") {
            vboxConstraints {
                margin = Insets(20.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }

        val filterType = combobox(values = FreqProcessType.values().toList()) {
            value = FreqProcessType.Idle
        }

        val filterRange = combobox(values = FreqProcessRange.values().toList()) {
            value = FreqProcessRange.BandPass
        }

        val centerFreqSlider =
            SliderWithSpinner(0.0, 1.0, ChangeListener { _, _, _ -> }, 0.01)
        val passStopBoundSlider =
            SliderWithSpinner(0.0, 1.0, ChangeListener { _, _, _ -> }, 0.01)


        buttonbar {
            padding = Insets(20.0, 10.0, 20.0, 10.0)
            button("Adjust").setOnAction {
                val centerFreq = centerFreqSlider.getSlider().value
                val passStopBound = passStopBoundSlider.getSlider().value

                // construct the corresponding filter
                val filter = when (filterType.value) {
                    FreqProcessType.Idle -> IdleFreqFilter(filterRange.value, centerFreq, passStopBound)
                    FreqProcessType.Gaussian -> GaussianFilter(filterRange.value, centerFreq, passStopBound)
                    FreqProcessType.ButterWorth -> ButterWorthFilter(filterRange.value, centerFreq, passStopBound)
                }

                engineController.frequencyTransfer(filter)
            }
            button("Reset").setOnAction {
                filterType.value = FreqProcessType.Idle
                filterRange.value = FreqProcessRange.BandPass
                centerFreqSlider.getSlider().value = 0.0
                passStopBoundSlider.getSlider().value = 0.0
            }
        }
    }
}