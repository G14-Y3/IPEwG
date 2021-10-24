#!/bin/bash
#SBATCH --gres=gpu:1
#SBATCH --mail-type=ALL # required to send email notifcations
#SBATCH --mail-user=xz1919 # required to send email notifcations - please replace <your_username> with your college login name or email address
USER=xz1919
cd /vol/bitbucket/${USER}
python3 -m virtualenv /vol/bitbucket/${USER}/myvenv
export PATH=/vol/bitbucket/${USER}/myvenv/bin/:$PATH
source activate
source /vol/cuda/11.0.3-cudnn8.0.5.39/setup.sh
pip --version #check your version of pip, pip3 is automatically used if your environment is python 3.x
# pip install tensorflow-gpu
pip install torch torchvision
TERM=vt100 # or TERM=xterm
/usr/bin/nvidia-smi
uptime