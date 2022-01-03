# Steganography

We have implemented a basic steganography tool that can encode image or text within another 
image without significantly altering the content of the image. We support different methods of 
steganography: encode image by its original dimension or by its pixel order. Both encode the information 
using the LSB technique.

Encode image by its original dimension will map the pixels of the encode image onto the provided image. 
This will produce a clear imprint of the encode image, which does not actually hide the encode image. 
The recommended way is to encode by its pixel order: it will turn the encode image into a one-dimensional 
array and encode them column-wise on the provided image. This will effectively hide the information.

Encoding text follows the same logic as described above.

You can also select how many bits to use when you want to encode image/text. Selecting 4 bits will retain 
the most information while not significantly altering the content image; whereas selecting 1 bit will lose
more information of the encode image. For text information, lower number of bits will not lose 
information, but limit the maximum number of text literals that can be encoded.
