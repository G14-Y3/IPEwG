# Image Processing Engine with GUI
Imperial College London Department of Computing Third Year Software Engineer Group Project
Supervisor: Dr. Pancham Shukla
Currently under development.

Original Overview          |  Histogram Equalization
:-------------------------:|:-------------------------:
![ori](https://github.com/G14-Y3/IPEwG/blob/master/Screenshot%202021-12-02%20at%2012.18.44.png) | ![histeq](https://github.com/G14-Y3/IPEwG/blob/master/Screenshot%202021-12-02%20at%2012.17.00.png)
Depth Estimation           |  Style Transfer
![depth](https://github.com/G14-Y3/IPEwG/blob/master/Screenshot%202021-12-02%20at%2012.19.16.png) | ![styletransfer](https://github.com/G14-Y3/IPEwG/blob/master/Screenshot%202021-12-02%20at%2012.19.35.png)

# How to Use

Since it is still under development, we have not yet packaged the software into a single bundle (e.g. dmg/exe/deb). Thus you have to have a JDK 17 environment.
Simply run `./gradlew run` and it will start the application, or use Intellij to open the project and configure the runner to be `gradle` with the `run` command.

# Development Process & Dependencies



# Feature Overview

This is an open-source image processing software built for scientific/research purposes. We include most common features that might be used frequently in 
researches or professional photo editing such as inverse color, greyscale, as well as advanced features such as neural style transfer and depth estimation. We 
also support different viewing methods, pipeline/import/export the sequence of processing, batch processing. Below is a list of features with detailed 
descriptions.

### Basic Processing Features

- [Inverse Color](https://github.com/G14-Y3/IPEwG/blob/master/docs/BasicProcessing.md#inverse-color)
- [Grayscale](https://github.com/G14-Y3/IPEwG/blob/master/docs/BasicProcessing.md#grayscale)
- [Black and White](https://github.com/G14-Y3/IPEwG/blob/master/docs/BasicProcessing.md#black-and-white)
- [Flip](https://github.com/G14-Y3/IPEwG/blob/master/docs/BasicProcessing.md#flip)
- [Rotation](https://github.com/G14-Y3/IPEwG/blob/master/docs/BasicProcessing.md#rotation)
- [Contrast](https://github.com/G14-Y3/IPEwG/blob/master/docs/BasicProcessing.md#contrast)
- [Color Adjustment](https://github.com/G14-Y3/IPEwG/blob/master/docs/BasicProcessing.md#colour-adjustment) (RGB and HSV)

### Advanced Processing Features

- [Edge Detection](https://github.com/G14-Y3/IPEwG/blob/master/docs/EdgeDetection.md)
- [Sharpen](https://github.com/G14-Y3/IPEwG/blob/master/docs/Sharpen.md)
- [Neural Style Transfer](https://github.com/G14-Y3/IPEwG/blob/master/docs/NeuralStyleTransfer.md)
- [Blur](https://github.com/G14-Y3/IPEwG/blob/master/docs/Blur.md)
- [Frequency Filtering](https://github.com/G14-Y3/IPEwG/blob/master/docs/FrequencyFilter.md)
- [Color Space Conversion](https://github.com/G14-Y3/IPEwG/blob/master/docs/ColorSpaceConversion.md)
- [Histogram Equalization](https://github.com/G14-Y3/IPEwG/blob/master/docs/HistogramEqualization.md)
- [Blending](https://github.com/G14-Y3/IPEwG/blob/master/docs/Blending.md)
- [Salt & Pepper Noise](https://github.com/G14-Y3/IPEwG/blob/master/docs/SaltAndPepper.md)
- [Depth Estimation](https://github.com/G14-Y3/IPEwG/blob/master/docs/DepthEstimation.md)
- [Watermarking](https://github.com/G14-Y3/IPEwG/blob/master/docs/Watermarking.md)
- [Denoise](https://github.com/G14-Y3/IPEwG/blob/master/docs/Denoise.md)
- [False Coloring](https://github.com/G14-Y3/IPEwG/blob/master/docs/FalseColouring.md)
- [Steganography](https://github.com/G14-Y3/IPEwG/blob/master/docs/Steganography.md)
- [Neural Network Visualization](https://github.com/G14-Y3/IPEwG/blob/master/docs/NeuralNetworkVisualization.md)
- [Posterize](https://github.com/G14-Y3/IPEwG/blob/master/docs/Posterize.md)
- [Rescaling](https://github.com/G14-Y3/IPEwG/blob/master/docs/Rescaling.md)

### More Features

- Support for different views: Slider and Parallel View
- Support for batch processing
- Support for multi-threaded processing
- Support import/expore sequences of operations

# Known Issues

- Rotation might lower the quality of image
