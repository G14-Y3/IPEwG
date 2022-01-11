# Known Issues

There are several bugs and known issuees that need to be addressed in future developments. This is not an exhaustive list that contains all the bugs/issues, but includes the most significant ones that might affect the functionality of the software.

For more detailed explanations, see the project report.

### Rotation might lower the quality of the image

Rotation  image  quality  deterioration:  naturally,  rotation  will  lower  the  quality  of  image  due  toimage aliasing.  The current implementation is to decompose the rotation transformation matrixinto three component to mitigate this effect. There are better techniques to tackle this problem.

### Better decoding mechanism for steganography

A  better  decoding  mechanism  for  steganography:  the  current  implementation  of  steganographyuses the Least Significant Bit (LSB) methods mentioned in Section 3.1.3.  However, for decoding animage potentially with steganography performed on it, there needs to be a mechanism for detectingwhether the image has hidden information or not.  A naive way to achieve this is to insert a magic number at a particular location in the image, but there should be a better way to achieve this.  Thecurrent implementation will just assume the image has hidden image upon decoding

### Image panel closed incorrectly in batch processing tab

Image  panel  collapsed  in  the  batch  processing  tab:  due  to  the  JavaFxTabdefinition,  it  is  notpossible  to  hide  the  image  panel  when  switching  to  the  batch  processing  tab.   This  becomes  anissue when trying to resize the window after switching to the batch processing tab:  the single imagepanel still exists, with no content inside.  A better GUI layout design could be designed to overcomethis issue.

### Changes in dimensions will cause slider view to display incorrectly

Transformations involving changing image dimensions cannot be displayed correctly in slider view:when performing transformations such as depth estimation or re-scaling that involves changing thedimension  of  the  image,  the  slider  view  does  not  display  the  image  correctly.   For  example,  thesliders will not work due to the change in dimension.

See the project report for more details.