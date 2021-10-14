package controller

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import models.IPEwGImage
import models.ImageModel
import processing.BasicFilter
import tornadofx.*
import java.io.File
import java.io.IOException
import java.net.URI
import javax.imageio.ImageIO

class ImageController : Controller() {
    lateinit var rawImage: Image
    lateinit var resultImage: WritableImage
    val activeImage = ImageModel(IPEwGImage())
    var isRaw = true

    init {
        load("./test_image.png")
        activeImage.image.set(rawImage)
    }

    fun save(path: String, format: String = "png") {
        val output = File(path)
        val buffer = SwingFXUtils.fromFXImage(resultImage, null)
        try {
            ImageIO.write(buffer, format, output)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun load(uri: URI) {
        load(uri.toString())
    }

    fun load(path: String) {
        val image = Image(path)
        rawImage = image
        resultImage = WritableImage(image.width.toInt(), image.height.toInt())
    }

    fun applyFilter() {
        val raw  = rawImage
        val image = WritableImage(raw.pixelReader, raw.width.toInt(), raw.height.toInt())
        BasicFilter.greyscaleFilter(image)
        resultImage = image
    }

    fun toggleActiveImage() {
        isRaw = !isRaw
        activeImage.image.set(if (isRaw) rawImage else resultImage)
    }
}