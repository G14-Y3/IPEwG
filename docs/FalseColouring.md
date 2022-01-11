# False Colouring

False Coloring is implemented using two different methods:  colour enhancement and colour transformation. Colour enhancement refers to the process of using the grayscale value of each pixel tolook  up  its  corresponding  false  colour.   In  this  method,  we  have  a  pre-defined  false  colour  set  (the `falseColorArray` in `FalseColoring.kt`) which is a mapping between pixels of certain grayscale valueto the corresponding false colour.  On the other hand, colour transformation refers to the idea of alteringthe RGB channels to form false colour. This method works by recalculating the RGB values of a pixel shown below.

See the project report for more details.