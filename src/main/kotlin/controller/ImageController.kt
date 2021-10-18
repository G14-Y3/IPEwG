package controller

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import models.FilterOperation
import models.IPEwGImage
import models.ImageModel
import processing.BasicFilter
import tornadofx.Controller
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

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

    fun applyFilter(op: FilterOperation) {
        val raw = resultImage.image
        val image = WritableImage(raw.pixelReader, raw.width.toInt(), raw.height.toInt())
        when (op) {
            FilterOperation.GREYSCALE -> BasicFilter.greyscaleFilter(image)
            FilterOperation.INVERSE_COLOR -> BasicFilter.inverseColorFilter(image)
            FilterOperation.MIRROR -> BasicFilter.mirrorFilter(image)
        }
        resultImage.image = image
        isRaw = false
        activeImage.image.set(resultImage.image)
    }

    fun toggleActiveImage() {
        isRaw = !isRaw
        activeImage.image.set(if (isRaw) rawImage.image else resultImage.image)
    }

    fun save(path: String, format: String) {
        val output = File(path)
        val buffer = SwingFXUtils.fromFXImage(resultImage.image, null)
        try {
            ImageIO.write(buffer, format, output)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}