package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.image.WritableImage
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import processing.frequency.*
import tornadofx.*
import view.CssStyle

class FrequencyTab : Fragment("Frequency Transfer") {

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

    private val engineController: EngineController by inject()

    override val root = vbox {
        label("Frequency Filtering") {
            vboxConstraints {
                margin = Insets(20.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }

        hbox {
            padding = Insets(10.0)
            textflow {
                text("Frequency filtering can extract the change in frequency of the pixels on the image. In " +
                        "the context of image processing, frequency refers to the rate of change in patterns or colour differences " +
                        "among nearby pixels. It is achieved by applying Fourier transform on the image. In this filter panel, you can " +
                        "choose 3 different filters to filter out high/low frequency regions. A low pass means to only keep low frequency " +
                        "parts of the image, whereas high pass means to only keep the high frequency parts of the image.")
            }
        }

        hbox {
            vbox {
                this.minWidth = 400.0
                hbox {
                    padding = Insets(10.0, 0.0, 0.0, 10.0)
                    label("Filter type") {
                        addClass(CssStyle.labelTag)
                        textAlignment = TextAlignment.CENTER
                    }
                    this.children.add(filterType)
                }

                hbox {
                    padding = Insets(10.0, 0.0, 0.0, 10.0)
                    label("Filter range") {
                        addClass(CssStyle.labelTag)
                        textAlignment = TextAlignment.CENTER
                    }
                    this.children.add(filterRange)
                }

                vbox {
                    padding = Insets(10.0, 0.0, 0.0, 5.0)
                    this.children.add(passStopBoundSlider.build())
                }

                vbox {
                    padding = Insets(10.0, 0.0, 0.0, 5.0)
                    this.children.add(bandWidthSlider.build())
                }

                vbox {
                    padding = Insets(10.0, 0.0, 0.0, 10.0)
                    this.children.add(orderBox)
                }
            }

            vbox {
                label("Filter Image") {
                    addClass(CssStyle.labelTag)
                }
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
                } else {
                    alert(
                        type = Alert.AlertType.ERROR,
                        header = "No Operation Selected",
                        content = "Select one frequency filter and range before click 'Adjust'",
                        ButtonType.OK
                    )
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