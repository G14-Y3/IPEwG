package models

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import processing.steganography.SteganographyDecoder
import processing.steganography.SteganographyEncoder
import tornadofx.*
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

class SteganographyModel : ViewModel() {

    val mainImageProperty = SimpleObjectProperty<Image>(null)
    private var mainImage by mainImageProperty

    val targetImageProperty = SimpleObjectProperty<Image>(null)
    private var targetImage by targetImageProperty

    val decodedText = SimpleStringProperty("")
    val imageToHide = SimpleObjectProperty<Image>(null)

    private var importedImage: Image? = null


    fun encodeText(
        text: String,
        key: String,
        bits: Int,
        onlyRChannel: Boolean
    ) {
        if (text.length > mainImage.width * mainImage.height) {
            alert(
                type = Alert.AlertType.ERROR,
                header = "Could not encode the text",
                content = "The text length exceeds the maximum amount of information that the original image can hold",
                ButtonType.OK
            )
            return
        }

        val transformation = SteganographyEncoder(text, onlyRChannel, key, bits)
        val destImage =
            WritableImage(
                mainImage.pixelReader,
                mainImage.width.toInt(),
                mainImage.height.toInt()
            )

        transformation.process(
            srcImage = WritableImage(
                mainImage.pixelReader,
                mainImage.width.toInt(),
                mainImage.height.toInt()
            ),
            destImage = destImage
        )
        targetImage = destImage
    }

    fun decodeText() {
        val transformation = SteganographyDecoder(false)
        val destImage =
            WritableImage(
                mainImage.pixelReader,
                mainImage.width.toInt(),
                mainImage.height.toInt()
            )

        transformation.process(
            srcImage = WritableImage(
                mainImage.pixelReader,
                mainImage.width.toInt(),
                mainImage.height.toInt()
            ),
            destImage = destImage
        )

        decodedText.value = transformation.get_result_text()
    }

    fun encodeImage(key: String, bits: Int, isByPixelOrder: Boolean) {
        val transformation = SteganographyEncoder(imageToHide.value, key, bits, isByPixelOrder)
        val destImage =
            WritableImage(
                mainImage.width.toInt(),
                mainImage.height.toInt()
            )

        transformation.process(
            srcImage = WritableImage(
                mainImage.pixelReader,
                mainImage.width.toInt(),
                mainImage.height.toInt()
            ),
            destImage = destImage
        )
        targetImage = destImage
    }

    fun decodeImage() {
        val transformation = SteganographyDecoder(true)
        val destImage =
            WritableImage(
                mainImage.pixelReader,
                mainImage.width.toInt(),
                mainImage.height.toInt()
            )

        transformation.process(
            srcImage = WritableImage(
                mainImage.pixelReader,
                mainImage.width.toInt(),
                mainImage.height.toInt()
            ),
            destImage = destImage
        )
        targetImage = transformation.get_result_image()
    }

    fun importMainImage(file: File) {
        importedImage = Image(file.toURI().toString())
        mainImage = importedImage
        targetImage = null
        decodedText.value = ""
    }

    fun useTargetAsSource() {
        mainImage = targetImage
        targetImage = null
        decodedText.value = ""
    }

    fun exportMainImage(file: File) {
        val buffer = SwingFXUtils.fromFXImage(targetImage, null)
        try {
            ImageIO.write(buffer, "png", file)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun importEncodeImage(file: File) {
        imageToHide.value = Image(file.toURI().toString())
    }

}