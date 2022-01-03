import javafx.geometry.Orientation
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import models.BatchProcessorModel
import tornadofx.*
import view.CssStyle
import view.FilterPanel
import view.ImagePanel
import view.TopBar


class GUI : View("IPEwG") {

    private val batchModel: BatchProcessorModel by inject()

    override val root = borderpane {
        setPrefSize(1600.0, 780.0)
        top<TopBar>()

        center = splitpane(
            Orientation.HORIZONTAL,
            find<FilterPanel>().root,
            find<ImagePanel>().root.managedWhen(batchModel.isBatchTabOpened)
                .visibleWhen(batchModel.isBatchTabOpened)
        ) {
            batchModel.isBatchTabOpened.addListener { _, _, newValue ->
                if (newValue) {
                    setDividerPosition(0, 0.7)
                } else {
                    setDividerPosition(0, 1.0)
                }
            }
        }
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

class ImageProcessor : App(GUI::class, CssStyle::class)

fun main(args: Array<String>) {
    launch<ImageProcessor>(args)
}