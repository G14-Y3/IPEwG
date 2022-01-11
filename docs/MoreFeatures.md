# More Additional Features

### Support for different views: Slider and Parallel View

We support two different mode to view the original image and processed image: slider view and parallel side-by-side view. Slider view is implemented by adding two sliders on the right and bottomof  the  displayed  image:   one  vertical  and  one  horizontal.   User  can  slide  them  to  form  four  differentregions:  top-left, top-right, bottom-left, bottom-right.  User can choose one of the four regions to displaythe original image.  Notice that we did not put relevant state/variables in the model because the sliderview  is  local  to  the  image  displaying  UI  and  does  not  involve  underlying  data  such  as  the  image  andthe transformations.  On the other hand, the parallel view is implemented by just putting two JavaFxImageViewpanel side by side.

### Support for multi-threaded processing and batch processing

Multi-threaded processing: From our experiments, we have noticed performance drops on large images.We have decided to implement multi-threaded processing for some of the operations to reduce the effectof this performance drop.  For simple operations such as black & white, flipping images, etc. We simply employed divide and conquering technique whereby we spawn a number of threads, giving each threada portion of the image to work on. More advanced operations require combining intermediate results of each threads before merging into final result. 

We support batch processing under the "Batch" tab, which contains the same set of image processing operation as in the single image processing tab. For batch processing,  we use an executor pool and pickeach image and operation and work on them in a pipeline fashion

### Support import/export sequences of operations

Import/Export sequences of operations:  As our users may often need to apply the same sequence oftransformations to a number of different images, we added the ability to export a list of transformationsas a JSON file, so that the same pipeline can be easily applied repeatably at a later time.  As all of our transformations already all implement the `ImageProcessing` interface, this involved declaring them as `Serializable`, giving them an unique `SerialName`, and finally marking which fields needs to be includedin the JSON file (if necessary).  Exporting the operations is then just using Kotlin’s `JsonBuilder` to write the list of operations as a JSON file.  Importing of operations involves decoding the JSON file as alist of objects, then applying the operations one after another.

See the project report for more details.