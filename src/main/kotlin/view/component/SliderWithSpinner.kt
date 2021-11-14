package view.component

import javafx.beans.property.Property
import javafx.beans.value.ChangeListener
import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.Slider
import javafx.scene.control.Spinner
import javafx.scene.layout.HBox
import tornadofx.*
import view.CssStyle
import kotlin.math.roundToInt

inline fun <reified T : Number> EventTarget.doubleSpinner(
    min: T? = null,
    max: T? = null,
    initialValue: T? = null,
    amountToStepBy: T? = null,
    editable: Boolean = false,
    property: Property<T>? = null,
    enableScroll: Boolean = false,
    noinline op: Spinner<T>.() -> Unit = {}
): Spinner<T> {
    val spinner = spinner(
        min = min,
        max = max,
        initialValue = initialValue,
        amountToStepBy = amountToStepBy,
        editable = editable,
        property = property,
        enableScroll = enableScroll,
        op = op,
    )

    // avoid NPE and set value to old value when user clear the field
    spinner.valueProperty()
        .addListener { _, old, new ->
            spinner.valueFactory.value = new ?: old
        }

    // use Regex to make sure user inputs a double not character string
    spinner.editor.textProperty()
        .addListener { _, old, new ->
            try {
                if (!new.matches(Regex("-?\\d*\\.?\\d*"))) {
                    spinner.editor.text = old
                } else {
                    spinner.editor.text =
                        new.toDouble().roundToInt().toString()
                }
            } catch (e: IllegalArgumentException) {
            }
        }

    return spinner
}

class SliderWithSpinner(
    private val minVal: Double,
    private val maxVal: Double,
    private val op: ChangeListener<Number>
) : HBox() {

    private var comboBox: ComboBox<*>? = null
    private lateinit var slider: Slider
    private lateinit var spinner: Spinner<Number>

    init {
        padding = Insets(20.0, 20.0, 10.0, 10.0)
        spacing = 20.0
        addClass(CssStyle.filterSlider)
        addClass(CssStyle.labelTag)
    }

    fun withLabel(labelName: String): SliderWithSpinner {
        val label = label(labelName) {
            addClass(CssStyle.labelTag)
        }
        this.add(label)
        return this
    }

    fun withComboBox(comboBox: ComboBox<*>): SliderWithSpinner {
        this.comboBox = comboBox
        this.add(comboBox)

        return this
    }

    fun getSlider(): Slider {
        return this.slider
    }

    fun build(): Node {
        slider = slider {
            min = minVal
            max = maxVal
            isShowTickMarks = true
            majorTickUnit = (maxVal - minVal) / 4
            minorTickCount = 1
            blockIncrement = 1.0
        }

        slider.value = 0.0
        slider.valueProperty().addListener(op)
        this.add(slider)

        spinner = spinner(
            min = minVal,
            max = maxVal,
            initialValue = 0.0,
            amountToStepBy = stepSize,
            editable = true,
            doubleProperty(0.0)
        ) {
            maxWidth = 70.0
        }
        
        try {
            slider.valueProperty().bindBidirectional(
                spinner.valueFactory.valueProperty()
            )
        } catch (e: NumberFormatException) {
        }

        this.add(spinner)

        return this
    }
}