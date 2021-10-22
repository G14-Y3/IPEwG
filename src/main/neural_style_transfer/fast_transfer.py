from torchvision.models import vgg16
from torch import nn
from zipfile import ZipFile
from torch.utils.data import Dataset, DataLoader
import torchvision.models as models
from torchvision.utils import save_image
from keras.preprocessing import image
from torch.utils.tensorboard import SummaryWriter
import torch
import cv2
import numpy

# run on local macos
# VVG16_WEIGHT_PATH = "/Users/zhaoxuan/.cache/torch/hub/checkpoints/vgg16-397923af.pth"
# TRANSNET_WEIGHT_PATH = "./fst.pth"
# COCO_DATASET_PATH = "/Users/zhaoxuan/Downloads/train2014.zip"
# STYLE_IMAGE_PATH  = "/Users/zhaoxuan/Downloads/style.jpg"
# OUTPUT_CHECKPOINT_PATH = f'/Users/zhaoxuan/Downloads/data/'

# run on DoC gpu cluster
VVG16_WEIGHT_PATH = "/vol/bitbucket/xz1919/vgg16-397923af.pth"
TRANSNET_WEIGHT_PATH = "/vol/bitbucket/xz1919/fst.pth"
COCO_DATASET_PATH = "/vol/bitbucket/xz1919/train2014.zip"
STYLE_IMAGE_PATH  = "/vol/bitbucket/xz1919/style.jpg"
OUTPUT_CHECKPOINT_PATH = f'/vol/bitbucket/xz1919/data/'

def load_image(path):
    image = cv2.imread(path)  # 打开图片
    image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)  # 转换通道，因为opencv默认读取格式为BGR，转换为RGB格式
    image = torch.from_numpy(image).float() / 255  # 数值归一化操作
    image = image.permute(2, 0, 1).unsqueeze(0)  # 换轴，（H,W,C）转换为（C,H,W），并做升维处理。
    return image

class COCODataSet(Dataset):

    def __init__(self):
        super(COCODataSet, self).__init__()
        self.zip_files = ZipFile(COCO_DATASET_PATH)
        self.data_set = []
        for file_name in self.zip_files.namelist():
            if not file_name.startswith('__MACOSX') and file_name.endswith('.jpg'):
                self.data_set.append(file_name)

    def __len__(self):
        return len(self.data_set)

    def __getitem__(self, item):
        file_path = self.data_set[item]
        image = self.zip_files.read(file_path)
        image = numpy.asarray(bytearray(image), dtype='uint8')
        image = cv2.imdecode(image, cv2.IMREAD_COLOR)
        image = cv2.resize(image, (512, 512), interpolation=cv2.INTER_AREA)
        image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
        image = torch.from_numpy(image).float() / 255
        image = image.permute(2, 0, 1)
        
        return image

class ResBlock(nn.Module):

    def __init__(self,c):
        super(ResBlock, self).__init__()
        self.layer = nn.Sequential(
            nn.Conv2d(c,c,3,1,1, bias=False),
            nn.InstanceNorm2d(c),
            nn.ReLU(),
            nn.Conv2d(c, c, 3, 1, 1, bias=False),
            nn.InstanceNorm2d(c),

        )
        self.relu = nn.ReLU()

    def forward(self, x):
        return self.relu(self.layer(x)+x)

class TransNet(nn.Module):

    def __init__(self):
        super(TransNet, self).__init__()
        self.layer = nn.Sequential(
            nn.Conv2d(3, 32, 9, 1, 4, bias=False),
            nn.InstanceNorm2d(32),
            nn.ReLU(),
            nn.Conv2d(32,64,3,2,1, bias=False),
            nn.InstanceNorm2d(64),
            nn.ReLU(),
            nn.Conv2d(64, 128, 3, 2, 1, bias=False),
            nn.InstanceNorm2d(128),
            nn.ReLU(),
            ResBlock(128),
            ResBlock(128),
            ResBlock(128),
            ResBlock(128),
            ResBlock(128),
            nn.Upsample(scale_factor=2, mode='nearest'),
            nn.Conv2d(128,64,3,1,1, bias=False),
            nn.InstanceNorm2d(64),
            nn.ReLU(),
            nn.Upsample(scale_factor=2, mode='nearest'),
            nn.Conv2d(64, 32, 3, 1, 1, bias=False),
            nn.InstanceNorm2d(32),
            nn.ReLU(),
            nn.Conv2d(32,3,9,1,4),
            nn.Sigmoid()
        )

    def forward(self, x):
        return self.layer(x)

def get_gram_matrix(f_map):
    n, c, h, w = f_map.shape
    f_map = f_map.reshape(n, c, h * w)
    gram_matrix = torch.matmul(f_map, f_map.transpose(1, 2))
    return gram_matrix

class VGG16(nn.Module):
    def __init__(self):
        super(VGG16, self).__init__()
        a = vgg16(True)
        a.load_state_dict(torch.load(VVG16_WEIGHT_PATH))
        a = a.features
        self.layer1 = a[:4]
        self.layer2 = a[4:9]
        self.layer3 = a[9:16]
        self.layer4 = a[16:23]
	
    """输出四层的特征图"""
    def forward(self, input_):
        out1 = self.layer1(input_)
        out2 = self.layer2(out1)
        out3 = self.layer3(out2)
        out4 = self.layer4(out3)
        return out1, out2, out3, out4

image_style = load_image(STYLE_IMAGE_PATH)
vgg16 = VGG16() #.cuda()
t_net = TransNet() #.cuda()
t_net.load_state_dict(torch.load(TRANSNET_WEIGHT_PATH))
# g_net = models.vgg16(pretrained=True)
# original: /Users/zhaoxuan/.cache/torch/hub/checkpoints/vgg16-397923af.pth
optimizer = torch.optim.Adam(t_net.parameters())
loss_func = nn.MSELoss() #.cuda()
data_set = COCODataSet()
batch_size = 2
data_loader = DataLoader(data_set, batch_size, True, drop_last=True)

"""计算分格,并计算gram矩阵"""
s1, s2, s3, s4 = vgg16(image_style)
s1 = get_gram_matrix(s1).detach().expand(batch_size,s1.shape[1],s1.shape[1])
s2 = get_gram_matrix(s2).detach().expand(batch_size,s2.shape[1],s2.shape[1])
s3 = get_gram_matrix(s3).detach().expand(batch_size,s3.shape[1],s3.shape[1])
s4 = get_gram_matrix(s4).detach().expand(batch_size,s4.shape[1],s4.shape[1])
j = 0
while True:
    for i, image in enumerate(data_loader):
        """生成图片，计算损失"""
        image_c = image #.cuda()
        image_g = t_net(image_c)
        out1, out2, out3, out4 = vgg16(image_g)
        # loss = loss_func(image_g, image_c)
        """计算风格损失"""
        loss_s1 = loss_func(get_gram_matrix(out1), s1)
        loss_s2 = loss_func(get_gram_matrix(out2), s2)
        loss_s3 = loss_func(get_gram_matrix(out3), s3)
        loss_s4 = loss_func(get_gram_matrix(out4), s4)
        loss_s = loss_s1+loss_s2+loss_s3+loss_s4

        """计算内容损失"""
        c1, c2, c3, c4 = vgg16(image_c)

        # loss_c1 = loss_func(out1, c1.detach())
        loss_c2 = loss_func(out2, c2.detach())
        # loss_c3 = loss_func(out3, c3.detach())
        # loss_c4 = loss_func(out4, c4.detach())

        """总损失"""
        loss = loss_c2 + 0.000000008 * loss_s

        """清空梯度、计算梯度、更新参数"""
        optimizer.zero_grad()
        loss.backward()
        optimizer.step()
        print(j, i, loss.item(), loss_c2.item(), loss_s.item())
        if i % 100 == 0:
            torch.save(t_net.state_dict(), TRANSNET_WEIGHT_PATH)
            save_image([image_g[0], image_c[0]], f'{OUTPUT_CHECKPOINT_PATH}{i}.jpg', padding=0, normalize=True,
                       range=(0, 1))
            j += 1
