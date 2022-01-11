# Denoise

We used two neural-network-based solutions for de-noising, both produce promising results. Additionally, user can specify the noise level to reduce in the second method.

Denoise  is  implemented  using  two  different  neural  network  models:  DRUNet and  RIDnet. DRUNet  is  a  state-of-art  network  that  can  be  used  in  image  denoising,  deblurring,  and  even  super-resolution.   Along  with  the  input  image,  a  noise  level  must  be  supplied  to  the  network  in  order  todetermine  the  level  that  denoising  will  take  place. The other network architecture, RIDnet, used four Embedded Atom Models (EAM) to perform denoisewithout needing to specify a noise level to reduce.

See the project report for more details.