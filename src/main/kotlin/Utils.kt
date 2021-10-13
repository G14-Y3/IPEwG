// constants for one image's displaying window
const val WINDOW_W_H_RATIO = 1.0
const val WINDOW_WIDTH = 400.0
const val WINDOW_HEIGHT = WINDOW_WIDTH * WINDOW_W_H_RATIO

fun cast(min: Double, max: Double, v: Double): Double {
    if (v < min) {
        return min
    } else if (v > max) {
        return max
    }
    return v
}