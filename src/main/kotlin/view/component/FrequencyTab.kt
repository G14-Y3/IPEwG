package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.image.WritableImage
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import processing.frequency.*
import tornadofx.*
import view.CssStyle

class FrequencyTab(
    private val engineController: EngineController) : VBox() {

    // type select combobox
    val filterType = combobox(values = FreqProcessType.values().toList()) {
        // display order input box only when type is butterworth
        valueProperty().addListener(ChangeListener { _, _, typeName ->
            orderBox.isVisible = typeName == FreqProcessType.ButterWorth
        })
    }
    // range select combobox
    val filterRange = combobox(values = FreqProcessRange.values().toList()) {
        // display bandwidth sliders only when required by the selected filter type/range
        valueProperty().addListener(ChangeListener { _, _, rangeName ->
            bandWidthSlider.isVisible = rangeName == FreqProcessRange.BandPass || rangeName == FreqProcessRange.BandReject
        })
    }

    // slider for adjusting filter parameters
    val passStopBoundSlider =
        SliderWithSpinner(0.0, 1.0, ChangeListener { _, _, _ -> }, 0.1)
            .withLabel("Cutoff Frequency")
    val bandWidthSlider =
        SliderWithSpinner(0.0, 1.0, ChangeListener { _, _, _ -> }, 0.1)
            .withLabel("Band Width")

    // input box for butterworth order
    val orderSpinner = spinner(
        min = 0.0,
        max = 10.0,
        initialValue = 0.0,
        amountToStepBy = 1.0,
        editable = true
    )
    val orderBox = HBox(
        label("order") {addClass(CssStyle.labelTag)},
        orderSpinner
    )

    // imageView for showing filter, updated by passing to processing filter instance
    var filterImageView = imageview {
        fitHeight = 150.0
        fitWidth = 150.0
        preserveRatioProperty().set(true)
    }

    init {
        label("Frequency Filtering ") {
            vboxConstraints {
                margin = Insets(20.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }
        hbox {
            vbox {
                this.minWidth = 400.0
                hbox {
                    label("filter type ") { addClass(CssStyle.labelTag) }
                    this.children.add(filterType)
                }

                hbox {label("filter range") { addClass(CssStyle.labelTag) }
                    this.children.add(filterRange)
                }

                this.children.add(passStopBoundSlider.build())
                this.children.add(bandWidthSlider.build())

                this.children.add(orderBox)
            }

            hbox {
                label("filter image:") {addClass(CssStyle.labelTag)}
                this.children.add(filterImageView)
            }
        }
        buttonbar {
            padding = Insets(20.0, 10.0, 20.0, 10.0)
            button("Adjust").setOnAction {
                val bandWidth = bandWidthSlider.getSlider().value
                val passStopBound = passStopBoundSlider.getSlider().value

                // act only when operation is selected
                if (filterType.value != null && filterRange.value != null) {

                    val freqFilter = FrequencyFilters(filterType.value, filterRange.value, passStopBound, bandWidth, orderSpinner.value.toInt())
                    engineController.frequencyTransfer(freqFilter)
                    setFilterImage(freqFilter)
                }
            }
            button("Reset").setOnAction {
                filterType.value = null
                filterRange.value = null
                bandWidthSlider.getSlider().value = 0.0
                passStopBoundSlider.getSlider().value = 0.0
                filterImageView.image = null
            }
        }
    }

    // update filter view to the new filterImage
    private fun setFilterImage(freqFilter: FrequencyFilters) {
        val height = filterImageView.fitHeight.toInt()
        val width = filterImageView.fitWidth.toInt()
        val matrix = freqFilter.getFilter(height, width)

        // new image for the new filter
        val filterImage = WritableImage(height, width)
        val writer = filterImage.pixelWriter

        for (x in 0 until height) {
            for (y in 0 until width) {
                writer.setColor(x, y, Color.color(matrix[x][y], matrix[x][y], matrix[x][y]))
            }
        }

        filterImageView.image = filterImage
    }
}