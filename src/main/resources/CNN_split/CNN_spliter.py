import torch
from torchvision import transforms
import sys
from PIL import Image
import torch.nn as nn
import torchvision.models as models
import os

file = open("/Users/xushitong/Desktop/trial_net.log", "w")
file.write("executed")
file.write(sys.argv[1])
file.close()

image_mean = torch.tensor([0.485, 0.456, 0.406])
image_std = torch.tensor([0.229, 0.224, 0.225])

transform = transforms.Compose([
  transforms.Resize((150, 225)),
  transforms.ToTensor(),
  transforms.Normalize(image_mean, image_std)
])

img = transform(Image.open('./src/main/resources/test_image.png')).reshape((1, 3, 150, 225))
net = torch.load(sys.argv[1])
layer_num_digit = len(str(len(net))) # get maximum digit number for representing layer index
format = f"%{layer_num_digit}d"
'''
NOTE: the name of module direcotries and layer files cannot be changed, 
      since the importer use lexicographical order of file name to decide net order
'''
def construct(net, img, path): 
  os.makedirs(path, exist_ok=True)
  module_index = 0
  for module in net:
    if isinstance(module, torch.nn.modules.container.Sequential):
      construct(module, img, path + f"{format % (module_index)} module/")
    else:
      traced_module = torch.jit.trace(module, img)
      # TODO: append layer name 
      traced_module.save(path + f"{format % (module_index)} layer.pt")
    img = module(img)
    module_index += 1

'''
NOTE: the directory name cannot be changed, 
      since importer use this directory to read in translated net layers
'''
construct(net, img, "./src/main/resources/CNN_split/CNN_traced/")