import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import tornadofx.*
import java.io.File
import java.io.IOException
import java.net.URI
import javax.imageio.ImageIO

class ImageController : Controller() {
    private lateinit var raw: Image
    private lateinit var result: WritableImage

    fun load(uri: URI): ImageController {/**/
        raw = Image(uri.toString())
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