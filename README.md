# Image Processing Engine with GUI
Imperial College London Department of Computing Third Year Software Engineer Group Project
Supervisor: Dr. Pancham Shukla
Currently under development.

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

- Inverse Color
- Greyscale
- Black and White
- Flip horizontally or vertically
- Rotation
- Contrast
- Color Adjustment (RGB and HSV)

### Advanced Processing Features

- Edge Detection
- Sharpen
- Neural Style Transfer
- Blur
- Frequency Filtering
- Color Space Conversion
- Histogram Equalization
- Blending
- Salt & Pepper Noise
- Depth Estimation
- Water Mark
- Denoise
- False Coloring
- Steganography
- Visualization of Neural Network\
- Posterize
- Rescaling

### More Features

- Support for different views: Slider and Parallel View
- Support for batch processing
- Support for multi-threaded processing
- Support import/expore sequences of operations

# Known Issues

- Rotation might lower the quality of image
