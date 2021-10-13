import view.CssStyle
import tornadofx.*
import view.FilterPanel
import view.ImagePanel
import view.TopBar

class GUI : View("IPEwG") {
    val sample1 = "../test_image.png"
    override val root = borderpane {
        top<TopBar>()
        left<FilterPanel>()
        center = find<ImagePanel>(mapOf(ImagePanel::uri to sample1)).root
    }
}

class ImageProcessor : App(GUI::class, CssStyle::class)

fun main(args: Array<String>) {
    launch<ImageProcessor>(args)
}