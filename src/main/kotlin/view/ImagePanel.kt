package view

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Insets
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.ZoomEvent
import models.EngineModel
import tornadofx.*

const val WINDOW_W_H_RATIO = 1.0
const val WINDOW_WIDTH = 600.0
const val WINDOW_HEIGHT = WINDOW_WIDTH * WINDOW_W_H_RATIO

class ImagePanel : View() {
    private val zoomedWidth: DoubleProperty = SimpleDoubleProperty(WINDOW_WIDTH)

    private val engine: EngineModel by inject()

    override val root = vbox {
        scrollpane {
            val stack = stackpane {
                engine.oriView = imageview(engine.originalImage) {
                    isVisible = false
                    isPreserveRatio = true
                }
                engine.newView = imageview(engine.previewImage)

                setOnMouseClicked {
                    engine.oriView!!.isVisible = !engine.oriView!!.isVisible
                    engine.newView!!.isVisible = !engine.newView!!.isVisible
                }
            }


            // listen to zoomProperty to detect zoom in & out action
            this.addEventFilter(ZoomEvent.ANY) {
                var ratio = 1.0
                if (it.zoomFactor > 1) {
                    ratio = 1.01
                } else if (it.zoomFactor < 1) {
                    // only zoom out when image is smaller than original image
                    if (zoomedWidth.get() > WINDOW_WIDTH) {
                        ratio = 1 / 1.01
                    }
                }
                // update image size and left top coordinate according to the ratio
                zoomedWidth.value = zoomedWidth.value * ratio
                engine.oriView!!.fitWidth = zoomedWidth.get()
                engine.newView!!.fitWidth = zoomedWidth.get()
            }

            this.hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER;
            this.vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER;

            // TODO: Better way to toggle between the images
            stack.children.forEach { child ->
                run {
                    if (child is ImageView) {
                        child.fitWidth = zoomedWidth.value
                        child.isPreserveRatio = true
                    }
                }
            }

            this.minWidth = WINDOW_WIDTH
            this.maxWidth = WINDOW_WIDTH
            this.minHeight = WINDOW_HEIGHT
            this.maxHeight = WINDOW_HEIGHT

            vboxConstraints {
                margin = Insets(20.0)
            }
        }
    }
}