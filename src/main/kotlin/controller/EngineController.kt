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
import processing.frequency.FrequencyFilters
import processing.resample.Params
import processing.resample.Resample
import processing.resample.ResampleMethod
import processing.steganography.SteganographyEncoder
import processing.styletransfer.NeuralStyleTransfer
import processing.styletransfer.NeuralStyles
import processing.steganography.WaterMark
import processing.steganography.WaterMarkingTechnique
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

    val previewWidth: Double get() = engine.previewImage.value.width
    val previewHeight: Double get() = engine.previewImage.value.height

    fun grayscale() = engine.transform(Grayscale())

    fun edgeDetection() = engine.transform(EdgeDetection())

    fun inverseColour() = engine.transform(InverseColour())

    fun rgbFilter(factor: Double, type: RGBType) = engine.adjust(type.name, factor)

    fun hsvFilter(factor: Double, type: HSVType) = engine.adjust(type.name, factor)

    fun contrast(factor: Double) = engine.adjust("CONTRAST", factor)

    fun submitAdjustment() = engine.submitAdjustment()

    fun resetAdjustment() = engine.resetAdjustment()

    fun flipHorizontal() = engine.transform(FlipHorizontal())

    fun flipVertical() = engine.transform(FlipVertical())

    fun styleTransfer(style: NeuralStyles) = engine.transform(NeuralStyleTransfer(style))

    fun blur(radius: Double, type: BlurType) = engine.adjust(type.name, radius)

    fun posterize(level: Double) = engine.adjust("POSTERIZATION", level)

    fun frequencyTransfer(frequencyFilters: FrequencyFilters) = engine.transform(frequencyFilters)

    fun sharpen() = engine.transform(Sharpen())

    fun blend(type: BlendType) = engine.transform(Blend(engine.blendImage.value, type))

    fun encodeImage(encodeImage: Image, key: String, bits: Int, isByPixelOrder: Boolean) =
        engine.transform(SteganographyEncoder(encodeImage, key, bits, isByPixelOrder))

    fun encodeText(encodeText: String, key: String, bits: Int, onlyRChannel: Boolean) =
        engine.transform(SteganographyEncoder(encodeText, onlyRChannel, key, bits))

    fun waterMark(encodeImage: Image, horizontalGap: Int, verticalGap: Int, technique: WaterMarkingTechnique) = engine.transform(WaterMark(encodeImage, horizontalGap, verticalGap, technique))

    private fun convertColorSpace(source: ColorSpaceType, target: ColorSpaceType) =
        engine.transform(ConvertColorSpace(source, target))

    fun convertsRGBToLinearRGB() = convertColorSpace(ColorSpaceType.sRGB, ColorSpaceType.LinearRGB)

    fun convertLinearRGBTosRGB() = convertColorSpace(ColorSpaceType.LinearRGB, ColorSpaceType.sRGB)

    fun histogramEqualization(histogramEqualization: HistogramEqualization) = engine.transform(histogramEqualization)

    fun saltAndPepper(noiseRatio: Double, seed: Int) = engine.transform(SaltPepperNoise(noiseRatio, seed))

    fun resample(srcWidth: Int, srcHeight: Int, width: Int, height: Int, fromSRGB: Boolean, method: ResampleMethod, params: Params?) = engine.transform(Resample(srcWidth, srcHeight, width, height, fromSRGB, params, method), "preview", width.toDouble(), height.toDouble())

    fun depthEstimation(modelType: DepthEstimationModel, colormap: DepthColorMap, width: Double, height: Double) = engine.transform(DepthEstimation(modelType, colormap), "preview", width, height)

    fun denoise(denoiseMethod: DenoiseMethod, noise: Double) = engine.transform(Denoise(denoiseMethod, noise))

    fun falseColoring(coloringMethod: FalseColoringMethod) = engine.transform(FalseColoring(coloringMethod))

    fun blackAndWhite(threshold: Double) = engine.adjust("BLACK_AND_WHITE", threshold)

    fun rotate(angle: Double) = engine.adjust("ROTATION", angle)
    
    fun CNNVisualize(netName: String, imgShape: List<Int>, layerNum: Int, lineIndex: Int, channelNum: List<Int>) =
        engine.transform(CNNVisualization(netName, imgShape, layerNum, lineIndex, channelNum))

}