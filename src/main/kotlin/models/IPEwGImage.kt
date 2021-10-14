package models
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import java.io.File
import java.io.IOException
import java.net.URI
import javax.imageio.ImageIO
import tornadofx.*

class IPEwGImage(image: Image? = null) {
    val imageProperty = SimpleObjectProperty<Image>(this, "image", image)
    var image by imageProperty
}
