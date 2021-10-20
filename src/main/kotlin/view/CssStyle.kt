package view

import javafx.geometry.Pos
import tornadofx.*

class CssStyle : Stylesheet() {
    companion object {
        val checkBox by cssclass()
        val labelTag by cssclass()
        val filterSlider by cssclass()
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
    }
}