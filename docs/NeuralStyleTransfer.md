# Neural Style Transfer

We have implemented neural style transfer for six different style images. 
The neural style transfer will transform the content image into an image with the same dimension and the same style of the style image.

Both the style images and their corresponding model are stored under the folder `/src/main/resources/style_transfer_model`.
You can also choose to train and add your own style transfer dynamically by following the instructions below.

A training script is available in the folder `/src/main/kotlin/processing/styletransfer/training`. 
To run the training process, simply run `python3 fast_transfer_train.py`. 
After the training process, you will have a `.pth` file under the same folder. 
Then run `python3 fast_transfer.py` to use `torch.hit.trace` to trace out the weights of the model and produce a JIT 
version of the PyTorch model. Then you can rename and put the image and the model into the same folder. 
Go to the panel and select “add new style” to indicate the style image and model you want to use. 
Details of the training process is available below.

The training process requires a large data set of content image. You can use the [COCO dataset](https://cocodataset.org/#download) to 
train your style model. During the training process, you can specify the output folder to see the 
effect of running the training process after certain epochs.
