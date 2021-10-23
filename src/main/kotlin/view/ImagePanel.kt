package view

import com.sun.prism.image.ViewPort
import javafx.geometry.Insets
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.input.ZoomEvent
import models.EngineModel
import tornadofx.*

const val WINDOW_W_H_RATIO = 1.0
const val WINDOW_WIDTH = 600.0
const val WINDOW_HEIGHT = WINDOW_WIDTH * WINDOW_W_H_RATIO

class ImagePanel : View() {
    private val engine: EngineModel by inject()
    private var lastMousePoint: Point2D? = null

    private lateinit var oriView : ImageView
    private lateinit var newView : ImageView

    // Maximum range the left/top pixel coordinate can take,
    // calculated as Image height/width - viewport height/width
    private var excessWidth = 0.0
    var excessHeight = 0.0

    override val root = vbox {
        stackpane {
            val stack = stackpane {
                oriView = imageview(engine.originalImage) {
                    isVisible = false
                    isPreserveRatio = true
                }
                newView = imageview(engine.previewImage)

                val viewport = Rectangle2D(
                    .0,
                    .0,
                    oriView.image.width,
                    oriView.image.height,
                )
                updateViewPort(viewport)

                setOnMouseClicked {
                    oriView.isVisible = !oriView.isVisible
                    newView.isVisible = !newView.isVisible
                }
            }

            this.addEventFilter(ScrollEvent.SCROLL) {
                var leftTopX = oriView.viewport.minX - it.deltaX
                leftTopX = cast(leftTopX, 0.0, excessWidth)
                var leftTopY = oriView.viewport.minY - it.deltaY
                leftTopY = cast(leftTopY, 0.0, excessHeight)
                val viewport = Rectangle2D(
                    leftTopX,
                    leftTopY,
                    oriView.viewport.width,
                    oriView.viewport.height,
                )
                updateViewPort(viewport)
            }


            // listen to zoomProperty to detect zoom in & out action
            this.addEventFilter(ZoomEvent.ANY) {
                var ratio = 1.0
                val oldViewport = oriView.viewport!!
                if (it.zoomFactor > 1) {
                    ratio = 1.035
                } else if (it.zoomFactor < 1) {
                    // only zoom out when viewport is smaller than original image
                    if (oldViewport.width < oriView.image.width) {
                        ratio = 1 / 1.035
                    }
                }

                // update image origin so zoom on the mouse position
                var leftTopX = oldViewport.minX + localToImage(it.x * (1 - 1 / ratio))
                leftTopX = cast(leftTopX, 0.0, excessWidth)
                var leftTopY = oldViewport.minY + localToImage(it.y * (1 - 1 / ratio))
                leftTopY = cast(leftTopY, 0.0, excessHeight)

                val newViewport = Rectangle2D(
                    leftTopX,
                    leftTopY,
                    oldViewport.width / ratio,
                    oldViewport.height / ratio,
                )
                updateViewPort(newViewport)
            }

            // Drag image handlers
            fun stopDrag(event: MouseEvent) {
                lastMousePoint = null
            }
            addEventFilter(MouseEvent.MOUSE_EXITED, ::stopDrag)
            addEventFilter(MouseEvent.MOUSE_RELEASED, ::stopDrag)
            addEventFilter(MouseEvent.MOUSE_PRESSED) { lastMousePoint = Point2D(it.sceneX, it.sceneY) }

            addEventFilter(MouseEvent.MOUSE_DRAGGED) {
                if (lastMousePoint != null) {
                    val oldViewport = oriView.viewport

                    var leftTopX = oldViewport.minX - localToImage(it.sceneX - lastMousePoint!!.x)
                    leftTopX = cast(leftTopX, 0.0, excessWidth)
                    var leftTopY = oldViewport.minY - localToImage(it.sceneY - lastMousePoint!!.y)
                    leftTopY = cast(leftTopY, 0.0, excessHeight)

                    val newViewport = Rectangle2D(
                        leftTopX,
                        leftTopY,
                        oldViewport.width,
                        oldViewport.height,
                    )
                    updateViewPort(newViewport)

                    lastMousePoint = Point2D(it.sceneX, it.sceneY)
                }
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
        if (value < min) {
            return min
        } else if (value > max) {
            return max
        }
        return value
    }

    // update both images' viewport in engine
    private fun updateViewPort(viewPort: Rectangle2D) {
        oriView.viewport = viewPort
        newView.viewport = viewPort
        excessWidth = oriView.image.width - viewPort.width
        excessHeight = oriView.image.height - viewPort.height
    }

    // from scene / screen / stage / view coordinates to image coordinates
    private fun localToImage(value: Double): Double {
        val viewport = oriView.viewport!!
        val localToImage = WINDOW_WIDTH / viewport.width

        return value / localToImage
    }
}
