package processing

import javafx.scene.image.WritableImage
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import processing.conversion.ConvertColorSpace
import processing.filters.*
import processing.filters.blur.BoxBlur
import processing.filters.blur.GaussianBlur
import processing.filters.blur.LensBlur
import processing.filters.blur.MotionBlur
import processing.frequency.IdleFreqFilter
import processing.styletransfer.NeuralStyleTransfer

val jsonFormatter = Json {
    prettyPrint = true
    serializersModule = SerializersModule {
        polymorphic(ImageProcessing::class) {
            // Declare all image processors here
            subclass(Adjustment::class)
            subclass(BoxBlur::class)
            subclass(Contrast::class)
            subclass(ConvertColorSpace::class)
            subclass(Convolution::class)
            subclass(EdgeDetection::class)
            subclass(FlipHorizontal::class)
            subclass(FlipVertical::class)
            subclass(GaussianBlur::class)
            subclass(Grayscale::class)
            subclass(HSVIntensity::class)
            subclass(HistogramEqualization::class)
            subclass(IdleFreqFilter::class)
            subclass(InverseColour::class)
            subclass(LensBlur::class)
            subclass(MotionBlur::class)
            subclass(NeuralStyleTransfer::class)
            subclass(RGBIntensity::class)
            subclass(Sharpen::class)
            subclass(SpatialSeparableConvolution::class)
            subclass(Blend::class)
        }
    }
}

interface ImageProcessing {
    fun process(image: WritableImage)
}

