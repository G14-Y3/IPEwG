import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.stage.Stage
import org.testfx.framework.junit.ApplicationTest
import processing.conversion.ColorSpaceType
import processing.conversion.ConvertColorSpace
import processing.denoise.Denoise
import processing.denoise.DenoiseMethod
import processing.depthestimation.DepthColorMap
import processing.depthestimation.DepthEstimation
import processing.depthestimation.DepthEstimationModel
import processing.filters.*
import processing.frequency.FreqProcessRange
import processing.frequency.FreqProcessType
import processing.frequency.FrequencyFilters
import processing.rotation.Rotation
import processing.steganography.SteganographyDecoder
import processing.steganography.SteganographyEncoder
import processing.styletransfer.NeuralStyleTransfer
import processing.styletransfer.NeuralStyles
import kotlin.system.measureTimeMillis
import kotlin.test.Ignore
import kotlin.test.Test

class PerformanceTest: ApplicationTest() {
    override fun start(stage: Stage) {
    }

    @Test
    @Ignore
    fun performanceTest() {
        val imagePaths = listOf(
            "./test_images/test_120p.jpeg",
            "./test_images/test_240p.jpeg",
            "./test_images/test_360p.jpeg",
            "./test_images/test_480p.jpeg",
            "./test_images/test_720p.jpeg",
        )

        val images = imagePaths.map { path -> Image(path) }
        val auxImage = images[0]

        val transformers = listOf(
            ConvertColorSpace(ColorSpaceType.sRGB, ColorSpaceType.LinearRGB),
            Denoise(DenoiseMethod.DRUNET, 0.1),
            Denoise(DenoiseMethod.RIDNET, 0.1),
            DepthEstimation(DepthEstimationModel.NYU, DepthColorMap.Viridis),
            DepthEstimation(DepthEstimationModel.KITTI, DepthColorMap.Viridis),
            BlackAndWhite(0.5),
            Blend(auxImage, BlendType.OVERLAY),
            Blend(auxImage, BlendType.MULTIPLY),
            Blend(auxImage, BlendType.NORMAL),
            Contrast(0.5),
            Convolution(
                arrayOf(
                    arrayOf(0.5, 0.5, 0.5),
                    arrayOf(0.5, 0.5, 0.5),
                    arrayOf(0.5, 0.5, 0.5)
                )
            ),
            EdgeDetection(),
            FalseColoring(FalseColoringMethod.TRANSFORM),
            FalseColoring(FalseColoringMethod.ENHANCEMENT),
            FlipHorizontal(),
            FlipVertical(),
            Grayscale(),
            HistogramEqualization(RGBType.R),
            HistogramEqualization(HSVType.H),
            HistogramEqualization(LabColor.L),
            HistogramEqualization(GrayScaleColorSpace()),
            HSVIntensity(0.5, HSVType.H),
            InverseColour(),
            RGBIntensity(0.5, RGBType.R),
            SaltPepperNoise(0.5, 0),
            Sharpen(),
            SpatialSeparableConvolution(arrayOf(0.5, 0.5, 0.5), arrayOf(0.5, 0.5, 0.5)),
            FrequencyFilters(FreqProcessType.ButterWorth, FreqProcessRange.BandPass,0.5, 0.5, 0),
            FrequencyFilters(FreqProcessType.Gaussian, FreqProcessRange.BandPass,0.5, 0.5, 0),
            FrequencyFilters(FreqProcessType.Idle, FreqProcessRange.BandPass,0.5, 0.5, 0),
            Rotation(0.3),
//            SteganographyDecoder(true),
//            SteganographyEncoder("hello", true, "world", 4), // to be checked
        ) + NeuralStyles.values().map { transform -> NeuralStyleTransfer(transform) }

        for (image in images) {
            for (transformer in transformers) {
                val input = WritableImage(image.pixelReader, image.width.toInt(), image.height.toInt())
                val output = WritableImage(image.width.toInt(), image.height.toInt())

                System.gc()

                val elapsed = measureTimeMillis {
                    transformer.process(input, output)
                }

                println("${image.url}\t$transformer\t$elapsed")
            }
        }
    }
}