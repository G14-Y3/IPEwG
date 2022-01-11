# Image Processing Engine with GUI
Imperial College London Department of Computing Third Year Software Engineer Group Project (Group 14)

Supervisor: Dr. Pancham Shukla

***Please see our group [project report](https://github.com/G14-Y3/IPEwG/blob/master/docs/ProjectReport.pdf). It contains more details about the project, including the development process, project structure, benchmarking, and more information about each functionalities.***

Currently submitted and software has been packaged. This project is completed within approximately 9 weeks. Please refer to the How-to-use section for more details.

We want to create an image processing engine that does not only include common functionalities, but also include clearly defined advanced processing techniques that can be easily visualized. We want the transformation to be portable: user can export a sequence of transformation and import/apply those operations on different images later, including across different operating systems. We also expect this processing engine to include the visualization of the output of open-source machine learning models in order to allow researcher to compare the performance difference between various models. Ultimately, we want to create a work flow so that anyone who wants to implement a processing routine or include any models can easily do so.

Grayscale Overview          |  Histogram Equalization
:-------------------------:|:-------------------------:
![ori](https://github.com/G14-Y3/IPEwG/blob/master/docs/img/grayscale_demo.png) | ![histeq](https://github.com/G14-Y3/IPEwG/blob/master/docs/img/hist_eq_demo.png)
Depth Estimation           |  Style Transfer
![depth](https://github.com/G14-Y3/IPEwG/blob/master/docs/img/depth_estimation_demo.png) | ![styletransfer](https://github.com/G14-Y3/IPEwG/blob/master/docs/img/style_transfer_demo.png)

# How to Use

We have tried to package the software into executable format (e.g. dmg/exe/deb). However, due to the use of TornadoFx in Kotlin, it has taken significantly longer time than we expected. Thus the most recommended way for launching the software now is by running `gradlew` script in the root folder.

In Linux or MacOS, simply run `./gradlew run` and it will start the application, or use Intellij to open the project and configure the runner to be `gradle` with the `run` command. In Windows, run `./gradlew.bat run`. Remember to check the permission of the file `gradlew` or `gradlew.bat`.

Below is a list of issues we encountered when trying to package the software into a proper distribution:

1. GraavlVM has issue with GUI software who need reflection/unsafe will not work unless we compile the source code of these dependencies as well
https://github.com/oracle/graal/issues/2232
2. GraavlVM-native-image-plugin does not work. Not maintained.

3. FXLauncher does not work. It is not maintained.
4. Not sure how to use update4j, but looks not working as expected.
5. javapackager, might working but hard to use with Kotlin. Facing issues.
6. Launch4j may work, but is Windows only (actually it is easy to run on Linux anyway)

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
- [A better decoding mechanism for steganograph](https://github.com/G14-Y3/IPEwG/blob/master/docs/KnownIssues.md#better-decoding-mechanism-for-steganography)
- [Image panel collapsed in the batch processing tab](https://github.com/G14-Y3/IPEwG/blob/master/docs/KnownIssues.md#image-panel-closed-incorrectly-in-batch-processing-tab)
- [Changing image dimension will cause slider view to be displayed inappropriately](https://github.com/G14-Y3/IPEwG/blob/master/docs/KnownIssues.md#changes-in-dimensions-will-cause-slider-view-to-display-incorrectly)
