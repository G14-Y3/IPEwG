# Posterize

Color Quantization (Posterization) implements the k-means++ algorithm to divide the set of colours into  clusters.   We  used  the  redmean  distance instead  of  the  Euclidean  distance  for  color  distance calculation to approximate the human eye color perception better.  The original k-means algorithm hasthe problem that when k is small and the image has large areas of pure colours, the other colours with smallareas are likely to be ignored because many initial centroids falls into the same colour. K-means++ solves this problem by trying to spread out the initial centroids.  To improve the performance, the algorithm switches back to the original k-means when k is large.

See the project report for more details.