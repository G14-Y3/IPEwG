package models

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import tornadofx.ViewModel
import tornadofx.observableListOf
import java.io.File
import java.io.IOException
import java.nio.file.Paths
import javax.imageio.ImageIO


class BatchProcessorModel : ViewModel() {

    val engine: EngineModel by inject()
    val originalImages = observableListOf<Image>()
    val transformedImages = observableListOf<WritableImage>()

    private val importedImagesNames = mutableListOf<String>()

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
        transformedImages.forEach { image ->
            engine.transformations.forEach {
                it.process(image)
            }
        }
    }

    fun exportImages(dir: File) {
        transformedImages.forEachIndexed { i, image ->
            val outPath =
                Paths.get(dir.absolutePath, importedImagesNames[i] + "png")

            val buffer = SwingFXUtils.fromFXImage(image, null)
            try {
                ImageIO.write(buffer, "png", File(outPath.toUri()))
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }

}