package models

import javafx.beans.property.SimpleBooleanProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import tornadofx.ViewModel
import tornadofx.observableListOf
import java.io.File
import java.io.IOException
import java.nio.file.Paths
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import javax.imageio.ImageIO


class BatchProcessorModel : ViewModel() {

    private val engine: EngineModel by inject()
    val isBatchTabOpened = SimpleBooleanProperty(false)

    private val originalImages = observableListOf<Image>()

    val transformedImages = observableListOf<WritableImage>()
    val importedImagesNames = mutableListOf<String>()

    fun revert() {
        transformedImages.clear()
        originalImages.forEach {
            transformedImages.add(
                WritableImage(
                    it.pixelReader,
                    it.width.toInt(),
                    it.height.toInt()
                )
            )
        }
    }

    fun loadImage(file: File) {
        val original = Image(file.toURI().toString())
        originalImages.add(original)
        transformedImages.add(
            WritableImage(
                original.pixelReader,
                original.width.toInt(),
                original.height.toInt()
            )
        )
        importedImagesNames.add(file.nameWithoutExtension)
    }

    fun apply() {
        val transformations =
            engine.transformations.subList(0, engine.currIndex + 1)

        for (image in transformedImages) {
            transformations.forEach { transformation ->
                transformation.process(image)
            }
        }
    }

    fun exportImages(dir: File) {
        transformedImages.forEachIndexed { i, image ->
            val outPath =
                Paths.get(dir.absolutePath, importedImagesNames[i] + ".png")

            val buffer = SwingFXUtils.fromFXImage(image, null)
            try {
                ImageIO.write(buffer, "png", File(outPath.toUri()))
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }

    fun remove(vararg indices: Int) {
        indices.forEach {
            originalImages.removeAt(it)
            transformedImages.removeAt(it)
            importedImagesNames.removeAt(it)
        }
    }

}