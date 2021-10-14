package models

import tornadofx.*

class ImageModel(image: IPEwGImage): ItemViewModel<IPEwGImage>(image) {
    val image = bind(IPEwGImage::imageProperty)
}
