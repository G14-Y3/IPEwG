package controller

import models.EngineModel
import processing.RGBType
import processing.filters.Grayscale
import processing.filters.InverseColour
import processing.filters.RGBIntensity
import tornadofx.Controller

/** IMPORTANT:
 *
 *  Currently the functions in this class seem functionalities
 *  BUTTTTT they as we develop the engine,these should be like
 *  ActionHandler/EventHandler because this is a `Controller`
 *
 *  So a typical method would be like:
 *      fun onLoad(...): {// Do something...}
 *      fun onSubmitButtonClick(...) : {// Do something...}
 *
 *
 */
class EngineController : Controller() {

    private val engine: EngineModel by inject()

    init {
        load("./test_image.png")
    }

    fun load(path: String) = engine.load(path)

    fun save(path: String) = engine.save(path)

    fun grayscale() = engine.transform(Grayscale())

    fun inverseColour() = engine.transform(InverseColour())

    fun rgbFilter(factor: Double, type: RGBType) = engine.transform(RGBIntensity(factor, type))
}