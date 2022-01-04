package processing.conversion

import javafx.scene.paint.Color

fun sRGBToLinearByChannel(value: Double, channel: ColorChannels): Double =
    if (channel == ColorChannels.Alpha) value else {
        if (value <= 0.04045) value / 12.92
        else StrictMath.pow((value + 0.055) / 1.055, 2.4)
    }

fun linearTosRGBByChannel(value: Double, channel: ColorChannels): Double =
    if (channel == ColorChannels.Alpha) value else {
        if (value <= 0.04045 / 12.92) value * 12.92
        else 1.055 * StrictMath.pow(value, 1.0 / 2.4) - 0.055
    }

fun channelWiseConversion(
    color: Color,
    func: (Double, ColorChannels) -> Double
): Color = Color(
    func(color.red, ColorChannels.Red),
    func(color.green, ColorChannels.Green),
    func(color.blue, ColorChannels.Blue),
    func(color.opacity, ColorChannels.Alpha),
)