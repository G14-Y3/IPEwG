package controller

import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import models.IPEwGImage
import models.ImageModel
import models.Instruction
import processing.BasicFilter
import tornadofx.Controller
import java.util.*

class ImageController : Controller() {
    var rawImage: IPEwGImage
    var resultImage: IPEwGImage
    val activeImage = ImageModel(IPEwGImage())
    var instructions = LinkedList<Instruction>()
    var isRaw = true

    init {
        val defaultImage: Image = Image("./test_image.png")
        rawImage = IPEwGImage(defaultImage)
        resultImage = IPEwGImage(
            WritableImage(
                defaultImage.pixelReader,
                defaultImage.width.toInt(),
                defaultImage.height.toInt()
            )
        )
        load("./test_image.png")
        activeImage.image.set(rawImage.image)
    }

    fun load(path: String) {
        rawImage.load(path)
        val im: Image = rawImage.image
        resultImage.image = WritableImage(im.pixelReader, im.width.toInt(), im.height.toInt())
        isRaw = true
        activeImage.image.set(im)
    }

    fun applyFilter(op: Instruction) {
        val result = rawImage.image
        val writableImage = WritableImage(result.pixelReader, result.width.toInt(), result.height.toInt())
        if (op in instructions) {
            if (op.exclusive()) {
                instructions.remove(op)
            } else {
                // replace the instruction with new one, which may contain new parameter
                instructions.remove(op)
                instructions.add(op)
            }
        } else {
            instructions.add(op)
        }
        for (ins in instructions) {
            println(ins.toString())
            ins.apply(writableImage)
        }
        resultImage.image = writableImage
        isRaw = false
        activeImage.image.set(resultImage.image)
    }

    fun toggleActiveImage() {
        isRaw = !isRaw
        activeImage.image.set(if (isRaw) rawImage.image else resultImage.image)
    }
}