package view.component

import javafx.geometry.Insets
import javafx.scene.layout.HBox
import javafx.scene.text.FontWeight
import tornadofx.*

class EncodeTextTab : HBox() {
    init {
        hbox {
            vbox {
                label("Encode/Decode Text") {
                    vboxConstraints {
                        margin = Insets(10.0, 20.0, 0.0, 20.0)
                    }
                    style {
                        fontWeight = FontWeight.BOLD
                        fontSize = Dimension(20.0, Dimension.LinearUnits.px)
                    }
                }
                hbox {
                    vbox {
                        hboxConstraints {
                            margin = Insets(20.0)
                        }
                        label("Supply a text sequence in the text box \n " +
                                "on the right and encode with the options below") {
                            hboxConstraints {
                                marginTop = 5.0
                                marginBottom = 20.0
                            }
                        }
                        checkbox("with a random key") {
                            action {

                            }
                        }
                        hbox {
                            label("with the  ") {
                                hboxConstraints {
                                    marginTop = 5.0
                                }
                            }
                            combobox(values = listOf(1, 2, 3, 4)) {

                            }
                            label("  lower bits encoded") {
                                hboxConstraints {
                                    marginTop = 5.0
                                }
                            }
                        }
                        hbox {
                            label("encode by the  ") {
                                hboxConstraints {
                                    marginTop = 5.0
                                }
                            }
                            combobox(values = listOf("dimension", "pixel order")) {

                            }
                            label("  of the encode image") {
                                hboxConstraints {
                                    marginTop = 5.0
                                }
                            }
                        }
                        checkbox("store encoding information to image metadata") {

                        }
                        buttonbar {
                            vboxConstraints {
                                marginTop = 20.0
                            }
                            button("Encode")
                            button("Decode")
                        }
                    }
                    textarea {
                        prefWidth = 300.0
                        hboxConstraints {
                            hboxConstraints {
                                margin = Insets(10.0)
                            }
                        }
                    }
                }
            }
        }

    }
}