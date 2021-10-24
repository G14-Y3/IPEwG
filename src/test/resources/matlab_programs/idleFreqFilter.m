% experiment program to implement corss verification of frequency filter
img = im2double(rgb2gray(imread("test_image.png")));
[h, w, cnt] = size(img);
% move left top point to center
centered_img = zeros(h, w, cnt);
for i = 1:h
    for j = 1:w
        centered_img(i, j, :) = img(i, j, :).*(-1).^(i+j);
    end
end
% get dft of centered image
fft_result = fft2(centered_img);
% imshow(fft_result);
% define filter function
[x, y] = freqspace([h, w], 'meshgrid');
H = zeros(h, w, cnt);
for i = 1:h
    for j = 1:w
        d = sqrt(x(i, j).^2 + y(i, j).^2);
        if d < 0.4
            H(i, j, :) = 1;
        end
    end
end
% apply low pass filter to frequency image
fft_result = fft_result.*H;
figure(1);
subplot(1,2,1); title("displaying filtered frequency");
imshow(fft_result);
% ifft to get output
output = real(ifft2(fft_result));
% move center back
for i=1:h
    for j=1:w
        output(i, j, :) = output(i, j, :).*((-1).^(i+j));
    end
end

subplot(1,2,2); title("displaying output image");
imshow(output);
