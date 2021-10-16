package models

import tornadofx.ItemViewModel

class ImageModel(image: IPEwGImage) : ItemViewModel<IPEwGImage>(image) {
    val image = bind(IPEwGImage::imageProperty)
}
