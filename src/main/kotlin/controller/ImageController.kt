package controller

import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import models.FilterOperation
import models.IPEwGImage
import models.ImageModel
import processing.BasicFilter
import tornadofx.Controller

class ImageController : Controller() {
    var rawImage: IPEwGImage
    var resultImage: IPEwGImage
    val activeImage = ImageModel(IPEwGImage())
    var isRaw = true

    init {
        val defaultImage: Image = Image("./test_image.png")
        rawImage = IPEwGImage(defaultImage)
        resultImage = IPEwGImage(
            WritableImage(
                defaultImage.pixelReader,
                defaultImage.width.toInt(),
                defaultImage.height.toInt()
            )
        )
        load("./test_image.png")
        activeImage.image.set(rawImage.image)
    }

    fun load(path: String) {
        rawImage.load(path)
        val im: Image = rawImage.image
        resultImage.image = WritableImage(im.pixelReader, im.width.toInt(), im.height.toInt())
        isRaw = true
        activeImage.image.set(im)
    }

    fun applyFilter(op: FilterOperation, isChecked: Boolean) {
        val raw = resultImage.image
        val image = WritableImage(raw.pixelReader, raw.width.toInt(), raw.height.toInt())
        when (op) {
            FilterOperation.GREYSCALE -> BasicFilter.greyscaleFilter(image)
            FilterOperation.INVERSE_COLOR -> BasicFilter.inverseColorFilter(image)
        }
        resultImage.image = image
        isRaw = false
        if (isChecked) activeImage.image.set(resultImage.image) else activeImage.image.set(rawImage.image)
    }

    fun toggleActiveImage() {
        isRaw = !isRaw
        activeImage.image.set(if (isRaw) rawImage.image else resultImage.image)
    }
}