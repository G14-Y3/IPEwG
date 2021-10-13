import gui.IPEwGStyle
import tornadofx.*

class ImageProcessor : App(GUI::class, IPEwGStyle::class)

fun main(args: Array<String>) {
    launch<ImageProcessor>(args)
}