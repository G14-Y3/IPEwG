package processing.filters

import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import processing.ImageProcessing
import kotlin.random.Random

/**
 * @param noiseRatio: *expected* percentage of pixels overwritten to noise pixel, noise pixels are black or white
 * @param seed: seed used for all random number generations in this noise adder, same seed gives same noisy image
 */
@Serializable
@SerialName("SaltPepperNoise")
class SaltPepperNoise(val noiseRatio: Double, val seed: Int): ImageProcessing {

    /**
     * Reference: https://www.imageeprocessing.com/2011/10/add-salt-and-pepper-noise-to-image.html
     * add salt & pepper noise to image,
     */
    override fun process(image: WritableImage) {
        val writer = image.pixelWriter
        val rand = Random(seed)
        for (x in 0 until image.width.toInt()) {
            for (y in 0 until image.height.toInt()) {
                val nextRand = rand.nextDouble(0.0,1.0)
                if (nextRand < noiseRatio) {
                    if (nextRand < noiseRatio / 2) {
                        writer.setColor(x, y, Color.color(0.0, 0.0, 0.0))
                    } else {
                        writer.setColor(x, y, Color.color(1.0, 1.0, 1.0))
                    }
                }
            }
        }
    }

    override fun toString(): String {
        return "Salt & Pepper Noise"
    }
}