package view

import controller.EngineController
import controller.FileController
import javafx.beans.binding.Bindings
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.control.Slider
import javafx.scene.control.TabPane
import javafx.scene.text.FontWeight
import models.EngineModel
import processing.HSVType
import processing.RGBType
import tornadofx.*

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
                                            min = 0.0
                                            max = 100.0
                                        }
                                        sliders += slider
                                        slider.value = 50.0
                                        slider.valueChangingProperty()
                                            .addListener(ChangeListener { _, _, _ -> op(slider.value / 50) })

                                        addClass(CssStyle.filterSlider)

                                        label {
                                            addClass(CssStyle.labelTag)
                                            textProperty().bind(
                                                Bindings.format(
                                                    "%.0f",
                                                    slider.valueProperty()
                                                )
                                            )
                                        }
                                    }

                                    buttonbar {
                                        padding = Insets(20.0, 10.0, 20.0, 10.0)
                                        button("Adjust").setOnAction {
                                            engineController.submitAdjustment()
                                            sliders.forEach { it.value = 50.0 }
                                        }
                                        button("Reset").setOnAction {
                                            engineController.resetAdjustment()
                                            sliders.forEach { it.value = 50.0 }
                                        }
                                    }
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
                        }
                    }
                    hbox {
                        alignment = Pos.CENTER
                        buttonbar {
                            padding = Insets(20.0, 10.0, 20.0, 10.0)
                            button("Undo").setOnAction { fileController.undo() }
                            button("Revert").setOnAction { fileController.revert() }
                        }
                    }
                }
                orientation = Orientation.VERTICAL
                setDividerPosition(0, 0.4)
            }
        }
    }
}