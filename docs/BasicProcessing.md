# Basic Processing Features

All the basic processing techniques are summarised below.

### Inverse Color
Inverse colour will perform `1.0 - RGBvalue` calculation on all three channels (RGB) of each pixel with opacity value unchanged.

### Grayscale
Grayscale will perform `0.21 * red + 0.71 * green + 0.07 * blue` calculation on each pixel and set all three channels (RGB) to this value with opacity unchanged.

### Black and White
Black and white will turn every pixel into black and white based on whether the grayscale value of the pixel is below/above a threshold that user can set. The calculation of grayscale value is the same as described in the Grayscale section above.

### Flip
Flip will mirror the image either horizontally or vertically. Horizontal flip will flip the columns of the image while vertical flip will flip the rows of the image. (e.g. exchange the first row/column and last row/column, exchange the second row/column and second but last row/column, etc.)

### Rotation
Rotation will rotate the image certain degree clockwise. It uses a rotation matrix to map the pixels from original image to the rotated image. Warning: the current implementation of rotation will reduce the image quality. Furture improvement is needed.

### Contrast
Contrast will perform the following calculation to each pixel

```
val newR = (factor * (oldColor.red - 0.5) + 0.5)
val newG = (factor * (oldColor.green - 0.5) + 0.5)
val newB = (factor * (oldColor.blue - 0.5) + 0.5)
```

where `factor` is a value between 0.0 and 1.0. User can adjust `factor` to apply different levels of contrast to the image.

### Colour Adjustment
Colour adjustment will allow user to change the RGB and HSV values of the image. The operation is as follows
```
[RGBHSV] = [oldValue] * (factor / 100.0 + 1)
```
where `factor` is a float between `-100.0` and `100.0`

See the project report for more details.