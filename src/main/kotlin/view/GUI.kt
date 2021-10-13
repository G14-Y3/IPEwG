import view.FilterPanel
import view.ImagePanel
import view.TopBar
import tornadofx.*

class GUI : View("IPEwG") {
    val sample1 = "../test_image.png"
    override val root = borderpane {
        top<TopBar>()
        left<FilterPanel>()
        center = find<ImagePanel>(mapOf(ImagePanel::uri to sample1)).root
    }
}