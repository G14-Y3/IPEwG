package view

import controller.EngineController
import controller.FileController
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.control.TabPane
import javafx.scene.text.FontWeight
import models.EngineModel
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

    private val basicFilterSliderList = mapOf(
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
                                basicFilterButtonList.map { (s, callback) -> button(s).setOnAction { callback() } }
                            }
                        }
                    }
                }
                tab("RGB") {
                    vbox {
                        label("Advanced Actions - RGB") {
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

                            basicFilterSliderList.map { (label, op) ->
                                hbox {
                                    padding = Insets(20.0, 20.0, 10.0, 10.0)
                                    label(label) {
                                        addClass(CssStyle.labelTag)
                                    }
                                    val slider = slider {
                                        min = 0.0
                                        max = 100.0
                                    }
                                    slider.value = 100.0
                                    slider.valueChangingProperty()
                                        .addListener(ChangeListener { _, _, _ -> op(slider.value / 100) })

                                    addClass(CssStyle.filterSlider)
                                }
                            }
                        }
                    }

                }

                tab("HSV") {

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