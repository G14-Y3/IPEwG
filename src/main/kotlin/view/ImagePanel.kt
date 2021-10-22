package view

import javafx.geometry.Insets
import javafx.geometry.Rectangle2D
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.input.ZoomEvent
import models.EngineModel
import tornadofx.*

const val WINDOW_W_H_RATIO = 1.0
const val WINDOW_WIDTH = 600.0
const val WINDOW_HEIGHT = WINDOW_WIDTH * WINDOW_W_H_RATIO

class ImagePanel : View() {
    private val engine: EngineModel by inject()

    override val root = vbox {
        scrollpane {
            val stack = stackpane {
                engine.oriView = imageview(engine.originalImage) {
                    isVisible = false
                    isPreserveRatio = true
                }
                engine.newView = imageview(engine.previewImage)

                val viewport = Rectangle2D(
                    .0,
                    .0,
                    engine.oriView!!.image.width,
                    engine.oriView!!.image.height,
                )
                engine.oriView!!.viewport = viewport
                engine.newView!!.viewport = viewport

                setOnMouseClicked {
                    engine.oriView!!.isVisible = !engine.oriView!!.isVisible
                    engine.newView!!.isVisible = !engine.newView!!.isVisible
                }
            }


            // listen to zoomProperty to detect zoom in & out action
            this.addEventFilter(ZoomEvent.ANY) {
                var ratio = 1.0
                val oldViewport = engine.oriView!!.viewport!!
                val localToImage = width / oldViewport.width
                if (it.zoomFactor > 1) {
                    ratio = 1.01
                } else if (it.zoomFactor < 1) {
                    // only zoom out when viewport is smaller than original image
                    if (oldViewport.width < engine.oriView!!.image.width) {
                        ratio = 1 / 1.01
                    }
                }

                // update image origin so zoom on the mouse position
                val newViewport = Rectangle2D(
                    oldViewport.minX + it.x * (1 - 1 / ratio) / localToImage,
                    oldViewport.minY + it.y * (1 - 1 / ratio) / localToImage,
                    oldViewport.width / ratio,
                    oldViewport.height / ratio,
                )
                engine.oriView!!.viewport = newViewport
                engine.newView!!.viewport = newViewport
            }

            this.hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER;
            this.vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER;

            // TODO: Better way to toggle between the images
            stack.children.forEach { child ->
                run {
                    if (child is ImageView) {
                        child.fitWidth = WINDOW_WIDTH
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