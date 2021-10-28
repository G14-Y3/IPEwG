package view.component

import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.control.ComboBox
import javafx.scene.control.Slider
import processing.BlurType
import tornadofx.*
import view.CssStyle

class SliderWithSpinner(property: String = "",
                        name: String,
                        labelOrComboBox: Any,
                        minVal: Double,
                        maxVal: Double,
                        op: (sliderValue: Double) -> Unit,
                        sliders: ArrayList<Slider>
) : View() {

    private var labelName = ""
    private var comboBox = ComboBox<BlurType>()

    init {
        if (labelOrComboBox is String) {
            labelName = labelOrComboBox.toString()
        } else if (labelOrComboBox is ComboBox<*>) {
            comboBox = labelOrComboBox as ComboBox<BlurType>
        }
    }

    override val root: Parent = hbox {
        padding = Insets(20.0, 20.0, 10.0, 10.0)
        spacing = 20.0

        if (property == "withLabel") {
            label(labelName) {
                addClass(CssStyle.labelTag)
            }
        } else if (property == "withComboBox") {
            this.add(comboBox)
        }

        val slider = slider {
            min = minVal
            max = maxVal
            isShowTickMarks = true
            majorTickUnit = (maxVal - minVal) / 4
            minorTickCount = 1
            blockIncrement = 1.0
        }
        sliders += slider
        slider.value = 0.0
        if (property == "withComboBox") {
            comboBox.valueProperty()
                .addListener(ChangeListener { _, _, _ ->
                    engineController.resetAdjustment()
                    slider.value = 0.0
                })
        }
        when (name) {
            "HSV", "RGB" -> slider.valueProperty()
                .addListener(ChangeListener { _, _, _ -> op(slider.value / 100 + 1) })
            "Blur" -> slider.valueProperty()
                .addListener(ChangeListener { _, _, _ ->
                    engineController.blur(
                        slider.value,
                        comboBox.value
                    )
                })
        }

        addClass(CssStyle.filterSlider)
        addClass(CssStyle.labelTag)

        val spinner = spinner(
            min = minVal,
            max = maxVal,
            initialValue = 0.0,
            amountToStepBy = 1.0,
            editable = true,
            doubleProperty(0.0)
        ) {
            maxWidth = 70.0
        }

        // avoid NPE and set value to old value when user clear the field
        spinner.valueProperty()
            .addListener(ChangeListener { _, old, new ->
                spinner.valueFactory.value = new ?: old
            })

        // use Regex to make sure user inputs a double not character string
        spinner.editor.textProperty()
            .addListener(ChangeListener<String> { _, old, new ->
                try {
                    if (!new.matches(Regex("-?\\d*\\.?\\d*"))) {
                        spinner.editor.text = old
                    } else {
                        spinner.editor.text =
                            new.toDouble().roundToInt().toString()
                    }
                } catch (e: IllegalArgumentException) {
                }
            })

        try {
            slider.valueProperty().bindBidirectional(
                spinner.valueFactory.valueProperty()
            )
        } catch (e: NumberFormatException) {
        }
    }
}