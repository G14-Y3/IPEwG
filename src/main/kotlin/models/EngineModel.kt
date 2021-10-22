package models

import javafx.beans.property.SimpleObjectProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import processing.ImageProcessing
import processing.filters.Adjustment
import tornadofx.ViewModel
import tornadofx.observableListOf
import java.io.File
import java.io.IOException
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

    var adjustmentProperties: MutableMap<String, Double> = HashMap()

    // Pipeline of transformations
    val transformations = observableListOf<ImageProcessing>()

    var updateListSelection: () -> Unit = {}

    // historical snapshots for quick undo-ing
    private val snapshots = mutableListOf<WritableImage>()
    var currIndex = -1

    fun load(path: String) {
        val image = Image(path)
        originalImage.value = image
        previewImage.value = image

        currIndex = -1
        transformations.clear()
        snapshots.clear()
    }

    fun save(path: String, format: String = "png") {
        val output = File(path)

        val buffer = SwingFXUtils.fromFXImage(previewImage.value, null)
        try {
            ImageIO.write(buffer, format, output)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun transform(transformation: ImageProcessing) {
        transformations.subList(currIndex + 1, transformations.size).clear()
        snapshots.subList(currIndex + 1, snapshots.size).clear()

        snapshots.add(
            WritableImage(
                previewImage.value.pixelReader,
                previewImage.value.width.toInt(),
                previewImage.value.height.toInt()
            )
        )
        transformations.add(transformation)
        currIndex++
        updateListSelection()
        transformation.process(snapshots[currIndex])
        previewImage.value = snapshots[currIndex]
    }

    /**
     * @param factor a value between 0.0 and 2.0
     */
    fun adjust(property: String, factor: Double) {
        adjustmentProperties[property] = factor
        val previous = if (currIndex < 0) originalImage.value else snapshots[currIndex]

        val preview = WritableImage(
            previous.pixelReader,
            previous.width.toInt(),
            previous.height.toInt()
        )
        Adjustment(adjustmentProperties).process(preview)
        previewImage.value = preview
    }

    fun submitAdjustment() {
        if (adjustmentProperties.isNotEmpty()) {
            transform(Adjustment(HashMap(adjustmentProperties)))
            adjustmentProperties.clear()
        }
    }

    fun resetAdjustment() {
        adjustmentProperties.clear()
        previewImage.value = if (currIndex < 0) originalImage.value else snapshots[currIndex]
    }

    fun undo() {
        if (currIndex < 0) return

        currIndex--
        updateListSelection()
        previewImage.value = if (currIndex < 0) originalImage.value else snapshots[currIndex]
    }

    fun redo() {
        if (currIndex == snapshots.size - 1) return

        currIndex++
        updateListSelection()
        previewImage.value = snapshots[currIndex]
    }

    fun setCurrentIndex(index: Int) {
        if (index < 0) return

        currIndex = index
        previewImage.value = snapshots[currIndex]
    }

    fun revert() {
        if (snapshots.isEmpty()) return

        snapshots.clear()
        transformations.clear()

        currIndex = -1
        updateListSelection()
        previewImage.value = originalImage.value
    }

}