# Image Processing Engine with GUI
Imperial College London Department of Computing Third Year Software Engineer Group Project

Group 14

Supervisor: Dr. Pancham Shukla

Currently submitted and software has been packaged. Please refer to the How-to-use section for more details.

Please see our group project report. It contains more details about the project, including the development process, benchmarking, and more information about each functionalities.

Original Overview          |  Histogram Equalization
:-------------------------:|:-------------------------:
![ori](https://github.com/G14-Y3/IPEwG/blob/master/Screenshot%202021-12-02%20at%2012.18.44.png) | ![histeq](https://github.com/G14-Y3/IPEwG/blob/master/Screenshot%202021-12-02%20at%2012.17.00.png)
Depth Estimation           |  Style Transfer
![depth](https://github.com/G14-Y3/IPEwG/blob/master/Screenshot%202021-12-02%20at%2012.19.16.png) | ![styletransfer](https://github.com/G14-Y3/IPEwG/blob/master/Screenshot%202021-12-02%20at%2012.19.35.png)

# How to Use

We have tried to package the software into executable format (e.g. dmg/exe/deb). However, due to the use of libtorch, it has taken significantly longer time than we expected. Thus you have to have a JDK 17 environment.
Simply run `./gradlew run` and it will start the application, or use Intellij to open the project and configure the runner to be `gradle` with the `run` command.

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

- [Support for different views: Slider and Parallel View](https://github.com/G14-Y3/IPEwG/blob/master/docs/MoreFeatures.md#support-for-different-views-slider-and-parallel-view)
- [Support for batch processing](https://github.com/G14-Y3/IPEwG/blob/master/docs/MoreFeatures.md#support-for-batch-processing)
- [Support for multi-threaded processing](https://github.com/G14-Y3/IPEwG/blob/master/docs/MoreFeatures.md#support-for-multi-threaded-processing)
- [Support import/expore sequences of operations](https://github.com/G14-Y3/IPEwG/blob/master/docs/MoreFeatures.md#support-importexport-sequences-of-operations)

# Known Issues

- [Rotation might lower the quality of image](https://github.com/G14-Y3/IPEwG/blob/master/docs/KnownIssues.md#rotation-might-lower-the-quality-of-the-image)
- [A better decoding mechanism for steganograph](https://github.com/G14-Y3/IPEwG/blob/master/docs/KnownIssues.md#rotation-might-lower-the-quality-of-the-image)
- [Image panel collapsed in the batch processing tab](https://github.com/G14-Y3/IPEwG/blob/master/docs/KnownIssues.md#rotation-might-lower-the-quality-of-the-image)
- [Changing image dimension will cause slider view to be displayed inappropriately](https://github.com/G14-Y3/IPEwG/blob/master/docs/KnownIssues.md#rotation-might-lower-the-quality-of-the-image)