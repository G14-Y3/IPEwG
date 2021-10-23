import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import tornadofx.*
import view.CssStyle
import view.FilterPanel
import view.ImagePanel
import view.TopBar

class GUI : View("IPEwG") {
    override val root = borderpane {
        setPrefSize(1100.0, 780.0)
        top<TopBar>()
        left<FilterPanel>()
        center<ImagePanel>()
    }

    override fun onDock() {
        currentWindow?.setOnCloseRequest {
            val quitText = "Quit"
            val result = alert(
                type = Alert.AlertType.CONFIRMATION,
                header = "Confirm Quit",
                content = "Are you sure you want to quit?",
                ButtonType.CANCEL,
                ButtonType(quitText, ButtonBar.ButtonData.OK_DONE),
            ).result
            if (result.text != quitText) {
                it.consume()
            }
        }
    }
}

class ImageProcessor : App(GUI::class, CssStyle::class) {
    override fun stop() {

        super.stop()
    }
}

fun main(args: Array<String>) {
    launch<ImageProcessor>(args)
}