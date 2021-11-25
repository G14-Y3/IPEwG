package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.text.FontWeight
import processing.denoise.DenoiseMethod
import tornadofx.*

class DenoiseTab: View("Denoise") {

    private val engineController: EngineController by inject()

    override val root: Parent = vbox {
        label("Denoise") {
            vboxConstraints {
                margin = Insets(20.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }
        hbox {
            vboxConstraints {
                margin = Insets(0.0, 20.0, 5.0, 10.0)
            }
            label("You can choose to ") {
                hboxConstraints {
                    marginTop = 5.0
                }
            }

            button("Denoise Using Ridnet") {
                action {
                    engineController.denoise(DenoiseMethod.RIDNET, 0.0)
                }
            }
        }

        hbox {
            var noiseLevel = 0.0
            vbox {
                hbox {
                    prefWidth = 300.0
                    label("Or you can adjust the noise level") {
                        vboxConstraints {
                            margin = Insets(10.0, 20.0, 5.0, 10.0)
                        }
                    }
                    vboxConstraints {
                        margin = Insets(10.0, 20.0, 10.0, 10.0)
                    }
                }
                val slider = SliderWithSpinner(0.0, 255.0, ChangeListener { _, _, new ->
                    noiseLevel = new as Double
                }).withLabel("Noise Level")
                this.children.add(slider.build())
                hbox {
                    vboxConstraints {
                        margin = Insets(10.0, 20.0, 10.0, 10.0)
                    }
                    label("and use ") {
                        hboxConstraints {
                            marginTop = 5.0
                        }
                    }
                    button("Denoise Using Drunet") {
                        vboxConstraints {
                            margin = Insets(20.0, 20.0, 10.0, 10.0)
                        }

                        action {
                            engineController.denoise(DenoiseMethod.DRUNET, noiseLevel)
                        }
                    }
                }
            }
        }
    }
}