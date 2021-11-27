package controller

import javafx.scene.image.Image
import models.EngineModel
import processing.conversion.ColorSpaceType
import processing.conversion.ConvertColorSpace
import processing.denoise.Denoise
import processing.denoise.DenoiseMethod
import processing.depthestimation.DepthColorMap
import processing.depthestimation.DepthEstimation
import processing.depthestimation.DepthEstimationModel
import processing.filters.*
import processing.steganography.SteganographyEncoder
import processing.frequency.FrequencyFilters
import processing.styletransfer.NeuralStyleTransfer
import processing.styletransfer.NeuralStyles
import processing.steganography.WaterMark
import processing.steganography.WaterMarkingTechnique
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
    
    fun frequencyTransfer(frequencyFilters: FrequencyFilters) = engine.transform(frequencyFilters)
    
    fun blur(radius: Int, type: BlurType) = engine.adjust(type.name, radius.toDouble())

    fun sharpen() = engine.transform(Sharpen())

    fun blend(type: BlendType) = engine.transform(Blend(engine.blendImage.value, type))

    fun encodeImage(encodeImage: Image, key: String, bits: Int, isByPixelOrder: Boolean) =
        engine.transform(SteganographyEncoder(encodeImage, key, bits, isByPixelOrder), "preview")

    fun encodeText(encodeText: String, key: String, bits: Int, onlyRChannel: Boolean) =
        engine.transform(SteganographyEncoder(encodeText, onlyRChannel, key, bits))

    fun waterMark(encodeImage: Image, horizontalGap: Int, verticalGap: Int, technique: WaterMarkingTechnique) = engine.transform(WaterMark(encodeImage, horizontalGap, verticalGap, technique))

    private fun convertColorSpace(source: ColorSpaceType, target: ColorSpaceType) =
        engine.transform(ConvertColorSpace(source, target))

    fun convertsRGBToLinearRGB() = convertColorSpace(ColorSpaceType.sRGB, ColorSpaceType.LinearRGB)

    fun convertLinearRGBTosRGB() = convertColorSpace(ColorSpaceType.LinearRGB, ColorSpaceType.sRGB)
    
    fun histogramEqualization(histogramEqualization: HistogramEqualization) = engine.transform(histogramEqualization)

    fun saltAndPepper(noiseRatio: Double, seed: Int) = engine.transform(SaltPepperNoise(noiseRatio, seed))

    fun depthEstimation(modelType: DepthEstimationModel, colormap: DepthColorMap) = engine.transform(DepthEstimation(modelType, colormap), "depth")

    fun denoise(denoiseMethod: DenoiseMethod, noise: Double) = engine.transform(Denoise(denoiseMethod, noise))

    fun blackAndWhite(threshold: Double) = engine.adjust("BLACK_AND_WHITE", threshold)
}