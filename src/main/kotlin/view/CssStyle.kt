package view

import javafx.geometry.Pos
import tornadofx.*
import java.awt.Color
import java.awt.Paint

class CssStyle : Stylesheet() {
    companion object {
        val checkBox by cssclass()
        val labelTag by cssclass()
        val filterSlider by cssclass()
        val topbarButton by cssclass()
    }

    init {
        checkBox {
            padding = CssBox<Dimension<Dimension.LinearUnits>>(.3.em, .3.em, .3.em, .3.em)
        }

        labelTag {
            padding = CssBox<Dimension<Dimension.LinearUnits>>(0.em, 0.5.em, 0.5.em, 0.5.em)
            prefWidth = Dimension<Dimension.LinearUnits>(100.0, Dimension.LinearUnits.px)
        }

        filterSlider {
            padding = CssBox<Dimension<Dimension.LinearUnits>>(0.em, 0.5.em, 0.em, 0.5.em)
        }

        listCell {
            alignment = Pos.CENTER
        }

        topbarButton {
            borderWidth = MultiValue(arrayOf(CssBox(0.em, 0.em, 0.em, 0.em)))
            backgroundColor = MultiValue(arrayOf(javafx.scene.paint.Paint.valueOf("transparent")))
            prefHeight = 20.px
        }

        hover {
            topbarButton {
                backgroundColor = MultiValue(arrayOf(javafx.scene.paint.Paint.valueOf("rgba(200, 200, 200, 0.8)")))
            }
        }
    }
}