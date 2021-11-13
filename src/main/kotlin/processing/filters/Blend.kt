/**
 * Blend class
 * Reference: https://dev.w3.org/SVG/modules/compositing/master/
 */
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
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

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
    NORMAL(applyToRGB { a, b, alphaA, alphaB -> a + b * (1 - alphaA) }),
    DISSOLVE({ colorA, colorB ->
        if (Random.nextDouble() <= colorA.opacity)
            Color(colorA.red, colorA.green, colorA.blue, 1.0)
        else colorB
    }),
    DARKEN(applyToRGB { a, b, alphaA, alphaB ->
        (a * alphaB).coerceAtMost(b * alphaA) + a * (1 - alphaB) + b * (1 - alphaA)
    }),
    MULTIPLY(applyToRGB(::multiplyBlend)),
    COLOR_BURN(applyToRGB(::colorBurnBlend)),
    LINEAR_BURN(applyToRGB(::linearBurnBlend)),
    LIGHTEN(applyToRGB { a, b, alphaA, alphaB ->
        (a * alphaB).coerceAtLeast(b * alphaA) + a * (1 - alphaB) + b * (1 - alphaA)
    }),
    SCREEN(applyToRGB(::screenBlend)),
    COLOR_DODGE(applyToRGB(::colorDodgeBlend)),
    LINEAR_DODGE(applyToRGB(::linearDodgeBlend)),
    OVERLAY(applyToRGB { a, b, alphaA, alphaB -> hardLightBlend(b, a, alphaB, alphaA) }),
    SOFT_LIGHT(applyToRGB { a, b, alphaA, alphaB ->
        val m = b / (alphaB + Double.MIN_VALUE)
        if (2 * a <= alphaA)
            b * (alphaA + (2 * a - alphaA) * (1 - m)) + a * (1 - alphaB) + b * (1 - alphaA)
        else if (2 * a > alphaA && 4 * b <= alphaB)
            alphaB * (2 * a - alphaA) * (16 * m.pow(3) - 12 * m.pow(2) - 3 * m) + a - a * alphaB + b
        else alphaB * (2 * a - alphaA) * (sqrt(m) - m) + a - a * alphaB + b
    }),
    HARD_LIGHT(applyToRGB(::hardLightBlend)),
    VIVID_LIGHT(applyToRGB { a, b, alphaA, alphaB ->
        if (a * 2 <= alphaA) colorBurnBlend(2 * a, b, alphaA, alphaB) +
                a * (1 - alphaB) - 2 * a * (1 - alphaB)
        else colorDodgeBlend(2 * (a - alphaA / 2), b, alphaA, alphaB) +
                a * (1 - alphaB) - 2 * (a - alphaA / 2) * (1 - alphaB)
    }),
    LINEAR_LIGHT(applyToRGB { a, b, alphaA, alphaB ->
        if (a * 2 <= alphaA) linearBurnBlend(2 * a, b, alphaA, alphaB) +
                a * (1 - alphaB) - 2 * a * (1 - alphaB)
        else linearDodgeBlend(2 * (a - alphaA / 2), b, alphaA, alphaB) +
                a * (1 - alphaB) - 2 * (a - alphaA / 2) * (1 - alphaB)
    }),
    DIFFERENCE(applyToRGB { a, b, alphaA, alphaB ->
        a + b - 2 * (a * alphaB).coerceAtMost(b * alphaA)
    }),
    EXCLUSION(applyToRGB { a, b, alphaA, alphaB ->
        a * alphaB + b * alphaA - 2 * a * b + a * (1 - alphaB) + b * (1 - alphaA)
    }),
//    HUE({ colorA, colorB -> TODO() }),
//    SATURATION({ colorA, colorB -> TODO() }),
//    COLOR({ colorA, colorB -> TODO() }),
//    LUMINOSITY({ colorA, colorB -> TODO() })
}

fun applyToRGB(channelOperation: (Double, Double, Double, Double) -> Double): (Color, Color) -> Color {
    return { oldColorA, oldColorB ->
        val alphaA = oldColorA.opacity
        val alphaB = oldColorB.opacity

        val aR = oldColorA.red * alphaA
        val bR = oldColorB.red * alphaB
        val aG = oldColorA.green * alphaA
        val bG = oldColorB.green * alphaB
        val aB = oldColorA.blue * alphaA
        val bB = oldColorB.blue * alphaB

        val newR = channelOperation(aR, bR, alphaA, alphaB)
        val newG = channelOperation(aG, bG, alphaA, alphaB)
        val newB = channelOperation(aB, bB, alphaA, alphaB)
        val newAlpha = alphaA + alphaB * (1 - alphaA)
        Color(
            (newR / newAlpha).coerceIn(0.0, 1.0),
            (newG / newAlpha).coerceIn(0.0, 1.0),
            (newB / newAlpha).coerceIn(0.0, 1.0),
            newAlpha.coerceIn(0.0, 1.0)
        )
    }
}

fun multiplyBlend(a: Double, b: Double, alphaA: Double, alphaB: Double): Double =
    a * b + a * (1 - alphaB) + b * (1 - alphaA)

fun screenBlend(a: Double, b: Double, alphaA: Double, alphaB: Double): Double =
    a + b - a * b

fun hardLightBlend(a: Double, b: Double, alphaA: Double, alphaB: Double): Double =
    if (a * 2 <= alphaA) 2 * a * b + a * (1 - alphaB) + b * (1 - alphaA)
    else alphaA * alphaB - 2 * (alphaA - a) * (alphaB - b) + a * (1 - alphaB) + b * (1 - alphaA)

fun colorBurnBlend(a: Double, b: Double, alphaA: Double, alphaB: Double) =
    if (b == alphaB && a == 0.0) alphaB * alphaA + b * (1 - alphaA)
    else if (a == 0.0) b * (1 - alphaA)
    else alphaB * alphaA * (1 - 1.0.coerceAtMost((1 - b / alphaB) * alphaA / a)) +
            a * (1 - alphaB) + b * (1 - alphaA)

fun colorDodgeBlend(a: Double, b: Double, alphaA: Double, alphaB: Double) =
    if (a == alphaA && b == 0.0) a * (1 - alphaB)
    else if (a == alphaA) alphaB * alphaA + a * (1 - alphaB) + b * (1 - alphaA)
    else alphaB * alphaA * 1.0.coerceAtMost(b / alphaB * alphaA / (alphaA - a)) +
            a * (1 - alphaB) + b * (1 - alphaA)

fun linearBurnBlend(a: Double, b: Double, alphaA: Double, alphaB: Double) =
    a + b - (alphaA + alphaB - 1).coerceAtLeast(0.0)

fun linearDodgeBlend(a: Double, b: Double, alphaA: Double, alphaB: Double) = a + b

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
