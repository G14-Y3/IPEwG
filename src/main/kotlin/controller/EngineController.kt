package controller

import models.EngineModel
import processing.GrayscaleFilter
import tornadofx.Controller

class EngineController : Controller() {

    private val engine: EngineModel by inject()
    private var isRaw = true

    val activeImage =
        if (isRaw) engine.originalImage else engine.transformedImage

    init {
        load("./test_image.png")
    }

    fun load(path: String) = engine.load(path)

    fun grayscale() = engine.transform(GrayscaleFilter())


}