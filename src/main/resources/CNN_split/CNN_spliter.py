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

def construct(net, img, path): 
  os.makedirs(path, exist_ok=True)
  module_index = 0
  for module in net:
    if isinstance(module, torch.nn.modules.container.Sequential):
      construct(module, img, path + f"module_{module_index}/")
    else:
      traced_module = torch.jit.trace(module, img)
      # TODO: append layer name 
      traced_module.save(path + f"layer_{module_index}.pt")
    img = module(img)
    module_index += 1

# vgg19 = models.vgg19(pretrained=True)
# net = nn.Sequential()
# for i in range(10):
#   net.add_module(f'layer {i}', vgg19.features[i])

# net.add_module("module", nn.Sequential(nn.AvgPool2d(1)))
net = torch.load(sys.argv[1])

construct(net, img, "./src/main/resources/CNN_split/CNN_traced/")