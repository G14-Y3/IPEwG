package view

import javafx.geometry.Insets
import tornadofx.*

class CssStyle : Stylesheet() {
    companion object {
        val buttonBox by cssclass()
        val labelTag by cssclass()
        val filterSlider by cssclass()
    }

    init {
        buttonBox {
            padding = CssBox<Dimension<Dimension.LinearUnits>>(5.px, 5.px, 5.px, 5.px)
        }

        labelTag {
            padding = CssBox<Dimension<Dimension.LinearUnits>>(0.px, 10.px, 10.px, 10.px)
            prefWidth = Dimension<Dimension.LinearUnits>(100.0, Dimension.LinearUnits.px)
        }

        filterSlider {
            padding = CssBox<Dimension<Dimension.LinearUnits>>(0.px, 10.px, 0.px, 10.px)
        }
    }
}