package view

import controller.EngineController
import controller.FileController
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.control.ComboBox
import javafx.scene.control.Slider
import javafx.scene.control.TabPane
import javafx.scene.layout.HBox
import javafx.scene.text.FontWeight
import models.EngineModel
import processing.BlurType
import processing.HSVType
import processing.RGBType
import tornadofx.*
import java.lang.IllegalArgumentException
import java.lang.NumberFormatException
import kotlin.math.roundToInt

class FilterPanel : View() {


    private val engine: EngineModel by inject()
    private val engineController: EngineController by inject()
    private val fileController: FileController by inject()

    private
    val basicFilterButtonList = mapOf(
        "Inverse Color" to engineController::inverseColour,
        "Greyscale" to engineController::grayscale,
        "Flip Horizontal" to engineController::flipHorizontal,
        "Flip Vertical" to engineController::flipVertical,
        "Edge Detection" to engineController::edgeDetection,
    )

    private val rgbFilterSliderList = mapOf(
        "R" to { factor: Double ->
            engineController.rgbFilter(
                factor,
                RGBType.R
            )
        },
        "G" to { factor: Double ->
            engineController.rgbFilter(
                factor,
                RGBType.G
            )
        },
        "B" to { factor: Double ->
            engineController.rgbFilter(
                factor,
                RGBType.B
            )
        },
    )

    private val hsvFilterSliderList = mapOf(
        "H" to { factor: Double ->
            engineController.hsvFilter(
                factor,
                HSVType.H
            )
        },
        "S" to { factor: Double ->
            engineController.hsvFilter(
                factor,
                HSVType.S
            )
        },
        "V" to { factor: Double ->
            engineController.hsvFilter(
                factor,
                HSVType.V
            )
        },
    )

    private val blurList = BlurType.values().toList()
    private val blurFilterSliderList = mapOf(
        BlurType.BOX to { factor: Double ->
            engineController.blur(
                factor,
                BlurType.BOX
            )
        }
    )

    private val adjustmentTabs = mapOf(
        "RGB" to rgbFilterSliderList,
        "HSV" to hsvFilterSliderList,
        "Blur" to blurFilterSliderList,
    )

    private fun generateSliderGUI(
        property: String,
        name: String,
        labelOrComboBox: Any,
        minVal: Double,
        maxVal: Double,
        op: (sliderValue: Double) -> Unit,
        sliders: ArrayList<Slider>
    ): HBox {
        var labelName = ""
        var comboBox = ComboBox<BlurType>()
        if (labelOrComboBox is String) {
            labelName = labelOrComboBox.toString()
        } else if (labelOrComboBox is ComboBox<*>) {
            comboBox = labelOrComboBox as ComboBox<BlurType>
        }

        return hbox {
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

    override
    val root = vbox {
        splitpane {
            scrollpane {
                val tabPane = tabpane {
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                    side = Side.LEFT
                    tab("Basic Actions") {
                        vbox {
                            label("Basic Actions") {
                                vboxConstraints {
                                    margin = Insets(20.0, 20.0, 10.0, 10.0)
                                }
                                style {
                                    fontWeight = FontWeight.BOLD
                                    fontSize = Dimension(20.0, Dimension.LinearUnits.px)

                                }
                            }

                            hbox {
                                padding = Insets(20.0, 20.0, 10.0, 10.0)
                                buttonbar {
                                    basicFilterButtonList.map { (s, callback) ->
                                        button(s) {
                                            /* The buttons need enough width to load up all labels
                                             in them, or the border will change when tabs clicked. */
                                            prefWidth = 120.0
                                        }.setOnAction { callback() }
                                    }
                                }
                            }
                        }
                    }

                    adjustmentTabs.map { (name, sliderList) ->
                        tab(name) {
                            vbox {
                                label(name) {
                                    vboxConstraints {
                                        margin = Insets(20.0, 20.0, 10.0, 10.0)
                                    }
                                    style {
                                        fontWeight = FontWeight.BOLD
                                        fontSize = Dimension(20.0, Dimension.LinearUnits.px)
                                    }
                                }

                                vbox {
                                    vboxConstraints {
                                        margin = Insets(10.0)
                                    }
                                    val sliders = ArrayList<Slider>()
                                    sliderList.map { (label, op) ->
                                        if (name == "RGB" || name == "HSV") {
                                            this.children.add(
                                                generateSliderGUI(
                                                    property = "withLabel",
                                                    name = name,
                                                    labelOrComboBox = label,
                                                    minVal = -100.0,
                                                    maxVal = 100.0,
                                                    op = op,
                                                    sliders = sliders
                                                )
                                            )
                                        } else if (name == "Blur") {
                                            val comboBox =
                                                combobox(values = blurList)
                                            comboBox.value = blurList[0]
                                            this.children.add(
                                                generateSliderGUI(
                                                    property = "withComboBox",
                                                    name = name,
                                                    labelOrComboBox = comboBox,
                                                    minVal = 0.0,
                                                    maxVal = 10.0,
                                                    op = op,
                                                    sliders = sliders
                                                )
                                            )
                                        } else {
                                            hbox {}
                                        }
                                    }
                                    buttonbar {
                                        padding = Insets(20.0, 10.0, 20.0, 10.0)
                                        button("Adjust").setOnAction {
                                            engineController.submitAdjustment()
                                            sliders.forEach { it.value = 0.0 }
                                        }
                                        button("Reset").setOnAction {
                                            engineController.resetAdjustment()
                                            sliders.forEach { it.value = 0.0 }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    tab("Sharpen") {
                        vbox {
                            vbox {
                                label("Sharpen") {
                                    vboxConstraints {
                                        margin = Insets(20.0, 20.0, 10.0, 10.0)
                                    }
                                    style {
                                        fontWeight = FontWeight.BOLD
                                        fontSize = Dimension(20.0, Dimension.LinearUnits.px)
                                    }
                                }
                            }
                            vbox {
                                vboxConstraints {
                                    margin = Insets(10.0)
                                }
                                buttonbar {
                                    button("Sharpen").setOnAction { engineController.sharpen() }
                                }
                            }
                        }
                    }
                }
                tabPane.selectionModel.selectedIndexProperty()
                    .addListener(ChangeListener<Number> { _, _, _ ->
                        this.vvalue = 0.0
                    })
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
