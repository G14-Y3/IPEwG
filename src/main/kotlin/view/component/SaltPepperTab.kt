package view.component

import controller.EngineController
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.layout.VBox
import javafx.scene.text.FontWeight
import kotlin.random.Random
import tornadofx.*
import view.CssStyle

class SaltPepperTab : Fragment("Salt Pepper") {
    private val engineController: EngineController by inject()

    private val noiseRatioSlider =
        SliderWithSpinner(0.0, 1.0, { _, _, _ -> }, 0.01)
            .withLabel("Noise Ratio: ")
    val seed = textfield()

    override val root: Parent = vbox {
        padding = Insets(20.0, 10.0, 20.0, 10.0)

        label("Salt & Pepper Noise") {
            style {
                fontWeight = FontWeight.BOLD
                fontSize = Dimension(20.0, Dimension.LinearUnits.px)
            }
        }

        hbox {
            padding = Insets(10.0)
            textflow {
                text("Salt & pepper refers to the white and black pixels. It simply adds random noises on the " +
                        "image, with the noise being either a white pixel or a black pixel. The randomness can be determined " +
                        "by passing in a seed number. This might be used to generate images with noises that can be used in " +
                        "generative adversarial neural network or benchmarking performance of de-noise filter.")
            }
        }

        this.children.add(noiseRatioSlider.build())
        hbox {
            padding = Insets(10.0, 0.0, 0.0, 12.0)
            label("Seed (optional):") {
                prefWidth = 130.0
            }
            this.children.add(seed)
        }

        hbox {
            padding = Insets(10.0)
            button("Add Noise").setOnAction {
                val seedRegex = "[-]?\\d*"
                if (seed.characters.toString() == "" || seed.characters.matches(seedRegex.toRegex())) {
                    var seedVal = Random.nextInt()
                    if (seed.characters.toString() != "") {
                        seedVal = Integer.parseInt(seed.characters.toString())
                    }

                    engineController.saltAndPepper(
                        noiseRatioSlider.getSlider().value,
                        seedVal
                    )

                } else {
                    alert(
                        type = Alert.AlertType.ERROR,
                        header = "Invalid Input",
                        content = "The seed input '${seed.characters}' does not match $seedRegex.\nInput should be natural number",
                        ButtonType.OK
                    )
                }
            }
        }
    }
}