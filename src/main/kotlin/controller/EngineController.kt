package controller

import javafx.scene.image.Image
import models.EngineModel
<<<<<<< HEAD
import processing.frequency.FrequencyFilters
import processing.BlurType
import processing.HSVType
import processing.RGBType
import processing.styletransfer.NeuralStyleTransfer
import processing.styletransfer.NeuralStyles
import processing.filters.*
import processing.frequency.FilterGenerator
import tornadofx.Controller
=======
import tornadofx.*
import processing.styletransfer.NeuralStyleTransfer
import processing.styletransfer.NeuralStyles
import processing.filters.*
import processing.frequency.IdleFreqFilter
import processing.steganography.SteganographyEncoder
>>>>>>> af26f97c8a583552bc11e2403267a79f049dcb15

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
<<<<<<< HEAD
    
    fun frequencyTransfer(filterGenerator: FilterGenerator) = engine.transform(FrequencyFilters(filterGenerator))
    
    fun blur(radius: Int, type: BlurType) = engine.adjust(type.name, radius.toDouble())
=======

    // todo: support user select different filter type and boundary
    fun frequencyTransfer() = engine.transform(IdleFreqFilter())
>>>>>>> af26f97c8a583552bc11e2403267a79f049dcb15

    fun sharpen() = engine.transform(Sharpen())
    
    fun encodeImage(encodeImage: Image, key: String, bits: Int, isByPixelOrder: Boolean) =
        engine.transform(SteganographyEncoder(encodeImage, key, bits, isByPixelOrder), "preview")

    fun encodeText(encodeText: String, key: String, bits: Int, onlyRChannel: Boolean) =
        engine.transform(SteganographyEncoder(encodeText, onlyRChannel, key, bits))
    
    fun histogramEqualization() = engine.transform(HistogramEqualization())
}