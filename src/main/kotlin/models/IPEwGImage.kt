package models

import javafx.beans.property.SimpleObjectProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import tornadofx.getValue
import tornadofx.setValue
import java.io.File
import java.io.IOException
import java.net.URI
import javax.imageio.ImageIO

class IPEwGImage(image: Image? = null) {
    val imageProperty = SimpleObjectProperty<Image>(this, "image", image)
    var image by imageProperty

    fun save(path: String, format: String = "png") {
        val output = File(path)
        val buffer = SwingFXUtils.fromFXImage(image, null)
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
        this.image = image
    }
}
