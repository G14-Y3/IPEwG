package models

import tornadofx.ItemViewModel

class FilterModel(filter: Filter) : ItemViewModel<Filter>(filter) {
    val R = bind(Filter::R)
    val G = bind(Filter::G)
    val B = bind(Filter::B)
}