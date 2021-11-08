package controller

import models.EngineModel
import processing.filters.*
import processing.frequency.IdleFreqFilter
import processing.styletransfer.NeuralStyleTransfer
import processing.styletransfer.NeuralStyles
import tornadofx.*

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

    fun grayscale() = engine.transform(Grayscale())

    fun edgeDetection() = engine.transform(EdgeDetection())

    fun inverseColour() = engine.transform(InverseColour())

    fun rgbFilter(factor: Double, type: RGBType) = engine.adjust(type.name, factor)

    fun hsvFilter(factor: Double, type: HSVType) = engine.adjust(type.name, factor)

    fun submitAdjustment() = engine.submitAdjustment()

    fun resetAdjustment() = engine.resetAdjustment()

    fun flipHorizontal() = engine.transform(FlipHorizontal())

    fun flipVertical() = engine.transform(FlipVertical())

    fun styleTransfer(style: NeuralStyles) = engine.transform(NeuralStyleTransfer(style))

    fun blur(radius: Double, type: BlurType) = engine.adjust(type.name, radius)

    // todo: support user select different filter type and boundary
    fun frequencyTransfer() = engine.transform(IdleFreqFilter())
    
    fun blur(radius: Int, type: BlurType) = engine.adjust(type.name, radius.toDouble())

    fun sharpen() = engine.transform(Sharpen())

    fun histogramEqualization() = engine.transform(HistogramEqualization())
}