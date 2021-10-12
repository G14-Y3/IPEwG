import tornadofx.*

class ImageProcessor: App(GUI::class)

fun main(args: Array<String>) {
    launch<ImageProcessor>(args)
}