package models

import javafx.beans.property.SimpleObjectProperty

class Filter {
    var R = SimpleObjectProperty<Double>(this, "", 1.0)
    var G = SimpleObjectProperty<Double>(this, "", 1.0)
    var B = SimpleObjectProperty<Double>(this, "", 1.0)
}