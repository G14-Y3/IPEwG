# CNN Visualization

CNN network visualization is implemented in 2 parts:  loading PyTorch CNN and displaying outputof layers.  Loading CNN is realized by accepting a file path to a pre-trained Convolution Neural Network,create a pickled file for each neural layer and write the information about displaying for each network layerto a metadata file.  The front end panel will display each layer’s information according to the metadatafile, and provide selections for user to see the output of one layer’s channel.  After selecting certain layer’schannel, the image is passed through the network layers, by using the pickled file constructed, to get theselected channel’s output.  Finally, the output tensor is normalized and displayed as a grayscale image.

See the project report for more details.