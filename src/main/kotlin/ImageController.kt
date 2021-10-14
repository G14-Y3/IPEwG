import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import tornadofx.*
import java.io.File
import java.io.IOException
import java.net.URL
import javax.imageio.ImageIO

class ImageController : Controller() {
    private lateinit var raw: Image
    private lateinit var result: WritableImage

    /* Load the image either by path (String) or by URL. */
    fun load(path: Any): ImageController {
        val imagePath = when (path) {
            is String -> "file:///$path"
            is URL -> path.toString()
            else -> throw IllegalArgumentException("Wrong type")
        }

        raw = Image(imagePath)
        result = WritableImage(raw.width.toInt(), raw.height.toInt())

        return this
    }

    fun save(path: String, format: String = "png"): ImageController {
        val output = File(path)
        val buffer = SwingFXUtils.fromFXImage(result, null)
        try {
            ImageIO.write(buffer, format, output)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        return this
    }

    fun get(raw: Boolean = false): Image = if (raw) this.raw else result
}