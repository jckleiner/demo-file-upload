import cv2
import sys


arg = sys.argv[1:][0]
print("first arg passed: " + arg)

#Read Image
img = cv2.imread(arg)

#Display Image
# cv2.imshow('image',img)
# cv2.waitKey(0)
# cv2.destroyAllWindows()

#Applying Grayscale filter to image
gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

#Saving filtered image to new file
cv2.imwrite('graytest.jpg',gray)

