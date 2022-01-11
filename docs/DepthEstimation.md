# Depth Estimation

We have implemented a neural-network-based depth estimation solution with two different models. User can also choose different colour maps to view the estimated depth.

Depth estimation is implemented using a model trained on two different dataset:  KITTI and NYUDepth v2 dataset. The depth estimation model consists of adensenet169encoder and a decoder withone 2D-convolution layer,  then four upsampling layers,  followed by another 2D-convolution layer.  Weused pre-trained models that were operated under keras and converted them into PyTorchptmodels.The  output  is  a  depth  map  with  a  dimension  ofw2byh2,  wherewandhare  the  original  size  of  theinput image.  In order to display the depth map, we used different color schemes provided in the `librarymahdilamb:colormap`.

See the project report for more details.