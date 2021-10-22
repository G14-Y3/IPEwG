package view

import com.sun.prism.image.ViewPort
import javafx.geometry.Insets
import javafx.geometry.Rectangle2D
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.input.ScrollEvent
import javafx.scene.input.ZoomEvent
import models.EngineModel
import tornadofx.*

const val WINDOW_W_H_RATIO = 1.0
const val WINDOW_WIDTH = 600.0
const val WINDOW_HEIGHT = WINDOW_WIDTH * WINDOW_W_H_RATIO

class ImagePanel : View() {
    private val engine: EngineModel by inject()

    override val root = vbox {
        stackpane {
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
                updateViewPort(viewport)

                setOnMouseClicked {
                    engine.oriView!!.isVisible = !engine.oriView!!.isVisible
                    engine.newView!!.isVisible = !engine.newView!!.isVisible
                }
            }

            this.addEventFilter(ScrollEvent.SCROLL) {
                var leftTopX = engine.oriView!!.viewport.minX - it.deltaX
                leftTopX = cast(leftTopX, 0.0, engine.excessWidth)
                var leftTopY = engine.oriView!!.viewport.minY - it.deltaY
                leftTopY = cast(leftTopY, 0.0, engine.excessHeight)
                val viewport = Rectangle2D(
                    leftTopX,
                    leftTopY,
                    engine.oriView!!.viewport.width,
                    engine.oriView!!.viewport.height,
                )
                updateViewPort(viewport)
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
                var leftTopX = oldViewport.minX + it.x * (1 - 1 / ratio) / localToImage
                leftTopX = cast(leftTopX, 0.0, engine.excessWidth)
                var leftTopY = oldViewport.minY + it.y * (1 - 1 / ratio) / localToImage
                leftTopY = cast(leftTopY, 0.0, engine.excessHeight)

                val newViewport = Rectangle2D(
                    leftTopX,
                    leftTopY,
                    oldViewport.width / ratio,
                    oldViewport.height / ratio,
                )
                updateViewPort(newViewport)
            }


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

    // cast given value in given range
    private fun cast(value : Double, min : Double, max : Double) : Double {
        if (value < 0) {
            return 0.0
        } else if (value > max) {
            return max
        }
        return value
    }

    // update both images' viewport in engine
    private fun updateViewPort(viewPort: Rectangle2D) {
        engine.oriView!!.viewport = viewPort
        engine.newView!!.viewport = viewPort
        engine.excessWidth = engine.oriView!!.image.width - viewPort.width
        engine.excessHeight = engine.oriView!!.image.height - viewPort.height
    }
}