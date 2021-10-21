package models

import javafx.beans.property.SimpleObjectProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import processing.ImageProcessing
import processing.filters.Adjustment
import tornadofx.ViewModel
import tornadofx.observableListOf
import java.io.File
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO
import kotlin.collections.HashMap

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

    var oriView : ImageView? = null
    var newView : ImageView? = null

    // The copy of the original image that we can work directly on
    private var transformedImage = WritableImage(
        originalImage.pixelReader,
        originalImage.width.toInt(),
        originalImage.height.toInt()
    )

    var adjustmentProperties: MutableMap<String, Double> = HashMap()

    // Pipeline of transformations
    val transformations = observableListOf<ImageProcessing>()

    // Stack of historical snapshots for quick undo-ing
    private val snapshots = Stack<WritableImage>()

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
        snapshots.push(
            WritableImage(
                transformedImage.pixelReader,
                transformedImage.width.toInt(),
                transformedImage.height.toInt()
            )
        )
        transformations.add(transformation)
        transformation.process(transformedImage)
        previewImage.value = transformedImage
    }

    /**
     * @param factor a value between 0.0 and 2.0
     */
    fun adjust(property: String, factor: Double) {
        adjustmentProperties[property] = factor

        val preview = WritableImage(
            transformedImage.pixelReader,
            transformedImage.width.toInt(),
            transformedImage.height.toInt()
        )
        Adjustment(adjustmentProperties).process(preview)
        previewImage.value = preview
    }

    fun submitAdjustment() {
        if (adjustmentProperties.isNotEmpty()) {
            transform(Adjustment(adjustmentProperties))
            adjustmentProperties.clear()
        }
    }

    fun resetAdjustment() {
        adjustmentProperties.clear()
        previewImage.value = transformedImage
    }

    fun undo() {
        if (snapshots.empty()) return

        transformations.removeLast()
        transformedImage = snapshots.pop()
        previewImage.value = transformedImage
    }

    fun revert() {
        if (snapshots.empty()) return

        transformedImage = snapshots.first()
        snapshots.clear()
        transformations.clear()

        previewImage.value = transformedImage
    }

}