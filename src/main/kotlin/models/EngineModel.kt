package models

import javafx.beans.property.SimpleObjectProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import processing.ImageProcessing
import tornadofx.ViewModel
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

class EngineModel(
    originalImage: Image = Image("./test_image.png"),
    transformedImage: WritableImage = WritableImage(
        originalImage.width.toInt(),
        originalImage.height.toInt()
    )
) : ViewModel() {

    val originalImage = SimpleObjectProperty(this, "originalImage", originalImage)

    val previewImage = SimpleObjectProperty(this, "previewImage", originalImage)

    val transformedImage =
        SimpleObjectProperty(this, "transformedImage", transformedImage)


    fun load(path: String) {
        val image = Image(path)
        originalImage.value = image
        previewImage.value = image
        transformedImage.value = WritableImage(
            image.pixelReader,
            image.width.toInt(),
            image.height.toInt()
        )
    }

    fun save(path: String, format: String = "png") {
        val output = File(path)
        val buffer = SwingFXUtils.fromFXImage(originalImage.value, null)
        try {
            ImageIO.write(buffer, format, output)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun transform(transformation: ImageProcessing) {
        transformation.process(transformedImage.value)
        previewImage.value = transformedImage.value
    }
}