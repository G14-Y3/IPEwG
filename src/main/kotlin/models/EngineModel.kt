package models

import javafx.beans.property.SimpleObjectProperty
import javafx.embed.swing.SwingFXUtils
import javafx.geometry.Rectangle2D
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import processing.ImageProcessing
import processing.filters.Adjustment
import processing.jsonFormatter
import processing.steganography.SteganographyDecoder
import tornadofx.ViewModel
import tornadofx.observableListOf
import view.ImagePanel
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.collections.set

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

    val encodeImage =
        SimpleObjectProperty(this, "encodeImage", originalImage)

    val decodeImage =
        SimpleObjectProperty(this, "decodeImage", originalImage)

    val blendImage =
        SimpleObjectProperty(this, "blendImage", originalImage)

    var adjustmentProperties: MutableMap<String, Double> = HashMap()

    // Pipeline of transformations
    val transformations = observableListOf<ImageProcessing>()

    var updateListSelection: () -> Unit = {}

    // historical snapshots for quick undo-ing
    private val snapshots = mutableListOf<WritableImage>()
    var currIndex = -1

    // all view components using this model, assigned in view port initialization
    var imagePanels = observableListOf<ImagePanel>()

    fun addImagePanel(imagePanel: ImagePanel) {
        imagePanels.add(imagePanel)
    }

    fun load(path: String) {
        val image = Image(path)
        originalImage.value = image
        previewImage.value = image
        for (imagePanel in imagePanels) {
            // update all image panel view ports, so that previous image viewport will be overwritten
            val viewport = Rectangle2D(
                .0,
                .0,
                image.width,
                image.height,
            )
            imagePanel.updateViewPort(viewport)
        }

        currIndex = -1
        transformations.clear()
        snapshots.clear()
    }

    fun loadEncodeImage(path: String) {
        val image = Image(path)
        encodeImage.value = image
    }

    fun loadBlendImage(path: String) {
        val image = Image(path)
        blendImage.value = image
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
        transform(transformation, "preview")
    }

    /* the @param destination here refers to where to put the transformed image: the image panel or the decode panel */
    fun transform(transformation: ImageProcessing, destination: String) {
        val previous = if (currIndex < 0) originalImage.value else snapshots[currIndex]
        when (destination) {
            "preview" -> {
                transformations.subList(currIndex + 1, transformations.size).clear()
                snapshots.subList(currIndex + 1, snapshots.size).clear()

                snapshots.add(
                    WritableImage(
                        previous.pixelReader,
                        previous.width.toInt(),
                        previous.height.toInt()
                    )
                )
                transformations.add(transformation)
                currIndex++
                updateListSelection()
                transformation.process(snapshots[currIndex])
                previewImage.value = snapshots[currIndex]
            }
            "decode" -> {
                if (transformation is SteganographyDecoder) {
                    val decoder: SteganographyDecoder = transformation
                    transformation.process(previous as WritableImage)
                    decodeImage.value = decoder.get_result_image()
                }
            }
        }
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

    fun loadJson(path: String) {
        revert()
        var jsonContent = ""
        File(path).useLines { lines -> jsonContent = lines.joinToString("") }
        val transformationList = jsonFormatter.decodeFromString<List<ImageProcessing>>(jsonContent)

        for (transformation in transformationList)
            transform(transformation)
    }

    fun saveJson(path: String) {
        File(path).printWriter().use { out ->
            out.println(jsonFormatter.encodeToString(ArrayList(transformations)))
        }
    }

}