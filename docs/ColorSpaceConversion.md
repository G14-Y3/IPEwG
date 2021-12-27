# Colour Space Conversion

We have implemented colour space transformation methods as the first and last step for a proper image processing pipeline. Since the image loaded via JavaFX is always in sRGB (as stated in the document of [`Color`](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/paint/Color.html) class, the default colour space is sRGB), only sRGB $\Longleftrightarrow$ Linear RGB model methods are implemented. It should be noted that many image processing methods assumed a linear RGB model (not applied with gamma), thus a correct result can only be obtained with this conversion involved, as a pipeline below:

![ColorSpaceConversion](./img/../ColorSpaceConversion-1.svg)

The necessity of processing under correct colour space is exemplified [here](http://www.ericbrasseur.org/gamma.html#introduction), [here](https://legacy.imagemagick.org/Usage/resize/#resize_colorspace) and [here (interactive)](https://entropymine.com/imageworsener/gamma/) for scaling.

As noted by [Alan Wolfe](https://twitter.com/Atrix256) in [his blog](https://blog.demofox.org/2018/03/10/dont-convert-srgb-u8-to-linear-u8/), the intermediate results are kept with `Double` rather than 8-bits for correct results with enough precision.
