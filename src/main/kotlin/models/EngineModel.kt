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
) : ViewModel() {

    // Reactive object reference to the 'original' image
    // 'original' here can either be:
    //   1. Image passed in when the class is instantiated or
    //   2. New image loaded using `load`
    val originalImage =
        SimpleObjectProperty(this, "originalImage", originalImage)

    // Reactive object reference to the transformed image
    val previewImage =
        SimpleObjectProperty(this, "previewImage", originalImage)

    // The copy of the original image that we can work directly on
    private var transformedImage = WritableImage(
        originalImage.width.toInt(),
        originalImage.height.toInt()
    )


    fun load(path: String) {
        val image = Image(path)
        originalImage.value = image
        previewImage.value = image
        transformedImage = WritableImage(
            image.pixelReader,
            image.width.toInt(),
            image.height.toInt()
        )
    }

    fun save(path: String, format: String = "png") {
        val output = File(path)
        val buffer = SwingFXUtils.fromFXImage(transformedImage, null)
        try {
            ImageIO.write(buffer, format, output)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun transform(transformation: ImageProcessing) {
        transformation.process(transformedImage)
        previewImage.value = transformedImage
    }
}