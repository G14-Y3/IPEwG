package view

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Insets
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.control.Slider
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.input.ZoomEvent
import models.EngineModel
import processing.multithread.splitImageHorizontal
import tornadofx.*
import java.util.concurrent.Executors

const val WINDOW_W_H_RATIO = 1.0
const val WINDOW_WIDTH = 600.0
const val WINDOW_HEIGHT = WINDOW_WIDTH * WINDOW_W_H_RATIO

class ImagePanel : View() {
    private val engine: EngineModel by inject()
    private var lastMousePoint: Point2D? = null

    lateinit var oriView: ImageView
    lateinit var newView: ImageView

    lateinit var slider: Slider

    // Maximum range the left/top pixel coordinate can take,
    // calculated as Image height/width - viewport height/width
    private var excessWidth = 0.0
    private var excessHeight = 0.0

    init {
        engine.addImagePanel(this)
    }

    override val root = vbox {
        alignment = Pos.CENTER
        stackpane {
            val stack = stackpane {
                oriView = imageview(engine.originalImage) {
                    isVisible = false
                    isPreserveRatio = true
                }
                newView = imageview(engine.parallelImage)


                val viewport = Rectangle2D(
                    .0,
                    .0,
                    oriView.image.width,
                    oriView.image.height,
                )
                updateViewPort(viewport)

                var dragging = false
                addEventHandler(MouseEvent.ANY) {
                    if (it.eventType == MouseEvent.MOUSE_PRESSED) {
                        dragging = false;
                    } else if (it.eventType == MouseEvent.DRAG_DETECTED) {
                        dragging = true;
                    } else if (it.eventType == MouseEvent.MOUSE_CLICKED) {
                        if (!dragging) {
                            oriView.isVisible = !oriView.isVisible
                            newView.isVisible = !newView.isVisible
                        }
                    }
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
            addEventFilter(MouseEvent.MOUSE_RELEASED, ::stopDrag)
            addEventFilter(MouseEvent.MOUSE_PRESSED) {
                lastMousePoint = Point2D(it.screenX, it.screenY)
            }

            addEventFilter(MouseEvent.MOUSE_DRAGGED) {
                if (lastMousePoint != null) {
                    val oldViewport = oriView.viewport

                    var leftTopX = oldViewport.minX - localToImage(it.screenX - lastMousePoint!!.x)
                    leftTopX = cast(leftTopX, 0.0, excessWidth)
                    var leftTopY = oldViewport.minY - localToImage(it.screenY - lastMousePoint!!.y)
                    leftTopY = cast(leftTopY, 0.0, excessHeight)

                    val newViewport = Rectangle2D(
                        leftTopX,
                        leftTopY,
                        oldViewport.width,
                        oldViewport.height,
                    )
                    updateViewPort(newViewport)

                    lastMousePoint = Point2D(it.screenX, it.screenY)
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

        // The slider at the bottom of the image view to slide between new and original image.
        slider = slider {
            maxWidth = WINDOW_WIDTH
            min = 0.0
            max = oriView.image.width
            blockIncrement = 1.0
        }

        slider.value = slider.max
        slider.valueProperty().addListener(ChangeListener { _, _, new ->
            engine.parallelView(new.toDouble())
        })
        print(slider.max)
    }

    fun sliderInit() {
        slider.value = slider.max
        print(slider.max)
    }

    fun updateSlider(newMax: Double) {
        slider.max = newMax
    }

    // cast given value in given range
    private fun cast(value: Double, min: Double, max: Double): Double {
        if (value < min) {
            return min
        } else if (value > max) {
            return max
        }
        return value
    }

    // update both images' viewport in engine
    fun updateViewPort(viewPort: Rectangle2D) {
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
