import torch
from torchvision import transforms
import sys
from PIL import Image
import torch.nn as nn
import torchvision.models as models
import os

'''
debug: run `python3 ./src/main/resources/CNN_split/CNN_spliter.py {TARGET NET PATH} {INPUT IMAGE SHAPE}` to see why import failed
           INPUT IMAGE SHAPE should be of format {int},{int}, ...
'''

shape = tuple(map(int, sys.argv[2].split(',')))
img = torch.zeros(shape)
net = torch.load(sys.argv[1])

'''
Metadata file
First line is path to imported CNN 
Each layer in CNN generate line: (separated by '|')
  module depth
  the layer name 
  shape of output from that layer (last 2 numbers are visualized image shape in one channel)
Each group of layer generate following line: (separated by '|')
  module depth
  module name
  (layer contained in module generate their lines after this module line)

'''
os.makedirs("./src/main/resources/CNN_split/CNN_traced/", exist_ok=True)
file = open("./src/main/resources/CNN_split/CNN_traced/.Metadata.log", "w")
file.write(sys.argv[1] + ' ' + sys.argv[2] + '\n')

'''
NOTE: the name of module direcotries and layer files cannot be changed, 
      since the importer use lexicographical order of file name to decide net order
'''
def construct(net, img, path, depth): 
  os.makedirs(path, exist_ok=True)
  layer_num_digit = len(str(len(net))) # get maximum digit number for representing layer index
  format = f"%{layer_num_digit}d"
  module_index = 0
  for module in net:
    if isinstance(module, torch.nn.modules.container.Sequential):
      file.write(f"{depth}|{module.__class__.__name__} module\n")
      construct(module, img, path + f"{format % (module_index)} module/", depth + 1)
      img = module(img)
    else:
      traced_module = torch.jit.trace(module, img)
      traced_module.save(path + f"{format % (module_index)} layer.pt")
      img = module(img)
      print("first pixels: ", img.reshape((-1, ))[:10])
      if len(img.shape) < 2:
        print(f"ERR: layer {module} output channel with shape {img.shape} not visualizable")
        exit -1
      else:
        file.write(f"{depth}|{module.__class__.__name__} layer")
        for d in img.shape:
          file.write(f"|{d}")
        file.write("\n")
    
    module_index += 1

'''
NOTE: the directory name cannot be changed, 
      since importer use this directory to read in translated net layers
'''
construct(net, img, "./src/main/resources/CNN_split/CNN_traced/", 0)


file.close()