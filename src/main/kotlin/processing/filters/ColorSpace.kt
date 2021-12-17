package processing.filters

import javafx.scene.paint.Color

interface ColorSpace {
    val range: Int

    /**
     * get the pixel color in the current color space
     */
    fun getter(pixel: Color): Double

    /**
     * set the value to the current color space
     * NOTE: HSVIntensity transformation should not use this method
     *       as they modify HSV value w.r.t. the original intensity
     */
    fun setter(pixel: Color, value: Double): Color
}