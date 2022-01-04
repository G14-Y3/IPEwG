package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.text.FontWeight
import processing.denoise.DenoiseMethod
import tornadofx.*
import java.awt.Desktop
import java.net.URL

class DenoiseTab: Fragment("Denoise") {

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
            padding = Insets(10.0)
            textflow {
                text("Denoise uses two different machine learning models to reduce the noises in the image. The " +
                        "two models are open-source and available at the links ")
                hyperlink("DRUNET") {
                    action {
                        Desktop.getDesktop().browse(URL("https://github.com/cszn/DPIR").toURI());
                    }
                }
                text(" and ")
                hyperlink("RIDNET") {
                    action {
                        Desktop.getDesktop().browse(URL("https://github.com/saeed-anwar/RIDNet").toURI());
                    }
                }
            }
        }

        hbox {
            padding = Insets(10.0)
            text("You can choose to  ")
            button("Denoise Using Ridnet") {
                action {
                    engineController.denoise(DenoiseMethod.RIDNET, 0.0)
                }
            }
            text("  or you can adjust the noise level")
        }

        var noiseLevel = 0.0

        vbox {
            val slider = SliderWithSpinner(0.0, 255.0, ChangeListener { _, _, new ->
                noiseLevel = new as Double
            }).withLabel("Noise Level")
            this.children.add(slider.build())
        }

        hbox {
            padding = Insets(10.0)
            textflow {
                text("and  ")
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