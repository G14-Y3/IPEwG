package view

import controller.EngineController
import controller.FileController
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.control.Slider
import javafx.scene.control.TabPane
import javafx.scene.text.FontWeight
import models.EngineModel
import processing.BlurType
import processing.HSVType
import processing.RGBType
import processing.styletransfer.NeuralStyleTransfer
import processing.styletransfer.NeuralStyles
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

    private val adjustmentTabs = mapOf(
        "RGB" to rgbFilterSliderList,
        "HSV" to hsvFilterSliderList,
    )

    override val root = vbox {
        splitpane {
            tabpane {
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
                                        prefWidth = 100.0
                                    }.setOnAction { callback() }
                                }
                            }
                        }
                    }
                }

                val style_transfer_map = mapOf(
                    "Van Gogh" to NeuralStyles.VAN_GOGH,
                    "Picasso" to NeuralStyles.PICASSO,
                    "Autumn" to NeuralStyles.AUTUMN,
                    "Google" to NeuralStyles.GOOGLE,
                    "UKIYOE" to NeuralStyles.UKIYOE,
                    "Abtract" to NeuralStyles.ABSTRACT
                )

                tab("Style Transfer") {
                    vbox{
                        style_transfer_map.map { (str, enum) ->
                            button(str) {
                                vboxConstraints {
                                    margin = Insets(10.0)
                                }
                                action {
                                    engineController.styleTransfer(enum)
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
                                    hbox {
                                        padding = Insets(20.0, 20.0, 10.0, 10.0)
                                        label(label) {
                                            addClass(CssStyle.labelTag)
                                        }
                                        val slider = slider {
                                            min = -100.0
                                            max = 100.0
                                            isShowTickMarks = true
                                            majorTickUnit = 50.0
                                            minorTickCount = 1
                                            blockIncrement = 1.0
                                        }
                                        sliders += slider
                                        slider.value = 0.0
                                        slider.valueProperty()
                                            .addListener(ChangeListener { _, _, _ -> op(slider.value / 100 + 1) })
                                        addClass(CssStyle.filterSlider)

                                        val spinner = spinner(
                                            -100.0,
                                            100.0,
                                            0.0,
                                            1.0,
                                            true,
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
                tab("Blur & Sharpen") {
                    vbox {
                        label("Blur") {
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
                            hbox {
                                padding = Insets(20.0, 20.0, 10.0, 10.0)
                                val blurList = BlurType.values().toList()
                                val combobox = combobox(values = blurList)
                                combobox.value = blurList[0]
                                val slider = slider {
                                    min = 0.0
                                    max = 10.0
                                }
                                sliders += slider
                                slider.value = 0.0
                                combobox.valueProperty()
                                    .addListener(ChangeListener { _, _, _ ->
                                        engineController.resetAdjustment()
                                        slider.value = 0.0
                                    })
                                slider.valueChangingProperty()
                                    .addListener(ChangeListener { _, _, _ ->
                                        engineController.blur(slider.value.toInt(), combobox.value)
                                    })

                                addClass(CssStyle.filterSlider)
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
                        label("Sharpen") {
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
                            buttonbar {
                                button("Sharpen").setOnAction { engineController.sharpen() }
                            }
                        }
                    }
                }

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
                        engine.updateListSelection = { selectionModel.select(engine.currIndex) }
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
