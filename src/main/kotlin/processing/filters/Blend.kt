@file:UseSerializers(ImageSerializer::class)

package processing.filters

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import processing.ImageProcessing
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.math.absoluteValue
import kotlin.math.sqrt

@Serializable
@SerialName("Blend")
class Blend(private val blendImage: Image, val mode: BlendType) :
    ImageProcessing {
    override fun process(image: WritableImage) {
        val width = image.width.toInt().coerceAtMost(blendImage.width.toInt())
        val height = image.height.toInt().coerceAtMost(blendImage.height.toInt())
        val readerA: PixelReader = blendImage.pixelReader
        val readerB: PixelReader = image.pixelReader
        val writer: PixelWriter = image.pixelWriter
        for (y in 0 until height) {
            for (x in 0 until width) {
                val oldColorA = readerA.getColor(x, y)
                val oldColorB = readerB.getColor(x, y)
                writer.setColor(x, y, mode.operation(oldColorA, oldColorB))
            }
        }
    }
}

enum class BlendType(val operation: (Color, Color) -> Color) {
    NORMAL(applyToRGB { a, _ -> a }),
    MULTIPLY(applyToRGB(::multiplyBlend)),
    SCREEN(applyToRGB(::screenBlend)),
    OVERLAY(applyToRGB { a, b -> hardLightBlend(b, a) }),
    DARKEN(applyToRGB { a, b -> a.coerceAtMost(b) }),
    LIGHTEN(applyToRGB { a, b -> a.coerceAtLeast(b) }),
    COLOR_DODGE(applyToRGB { a, b ->
        if (a < 1) 1.0.coerceAtMost(b / (1 - a))
        else 1.0
    }),
    COLOR_BURN(applyToRGB { a, b ->
        if (a > 0) 1 - 1.0.coerceAtMost((1 - b) / a)
        else 0.0
    }),
    HARD_LIGHT(applyToRGB(::hardLightBlend)),
    SOFT_LIGHT(applyToRGB { a, b ->
        if (a <= 0.5) b - (1 - 2 * a) * b * (1 - b)
        else b + (2 * a - 1) * (d(b) - b)
    }),
    DIFFERENCE(applyToRGB { a, b -> (b - a).absoluteValue }),
    EXCLUSION(applyToRGB { a, b -> b + a - 2 * b * a }),
//    HUE({ colorA, colorB -> TODO() }),
//    SATURATION({ colorA, colorB -> TODO() }),
//    COLOR({ colorA, colorB -> TODO() }),
//    LUMINOSITY({ colorA, colorB -> TODO() })
}

fun applyToRGB(channelOperation: (Double, Double) -> Double): (Color, Color) -> Color {
    return { oldColorA, oldColorB ->
        val newR = channelOperation(oldColorA.red, oldColorB.red)
        val newG = channelOperation(oldColorA.green, oldColorB.green)
        val newB = channelOperation(oldColorA.blue, oldColorB.blue)
        Color(
            oldColorA.opacity * newR + (1 - oldColorA.opacity) * oldColorB.red,
            oldColorA.opacity * newG + (1 - oldColorA.opacity) * oldColorB.green,
            oldColorA.opacity * newB + (1 - oldColorA.opacity) * oldColorB.blue,
            1 - (1 - oldColorB.opacity) * (1 - oldColorB.opacity)
        )
    }
}

fun multiplyBlend(a: Double, b: Double): Double = a * b
fun screenBlend(a: Double, b: Double): Double = 1 - (1 - a) * (1 - b)
fun hardLightBlend(a: Double, b: Double): Double =
    if (a <= 0.5) multiplyBlend(b, 2 * a)
    else screenBlend(b, 2 * a - 1)

fun d(x: Double): Double = if (x <= 0.25) ((16 * x - 12) * x + 4) * x else sqrt(x)

fun cloneColor(c: Color): Color = Color(c.red, c.green, c.blue, c.opacity)


object ImageSerializer : KSerializer<Image> {
    override val descriptor = PrimitiveSerialDescriptor("Image", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Image) {
        val s = ByteArrayOutputStream()
        ImageIO.write(SwingFXUtils.fromFXImage(value, null), "png", s)
        encoder.encodeSerializableValue(ByteArraySerializer(), s.toByteArray())
    }

    override fun deserialize(decoder: Decoder): Image {
        return SwingFXUtils.toFXImage(
            ImageIO.read(decoder.decodeSerializableValue(ByteArraySerializer()).inputStream()),
            null
        )
    }
}
