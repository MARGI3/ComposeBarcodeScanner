Jetpack Compose Barcode Scanner


[Jetpack Compose](https://developer.android.com/jetpack/compose) + [Google ML Kit - Barcode Scanning](https://developers.google.com/ml-kit/vision/barcode-scanning)

## ScreenRecording



https://user-images.githubusercontent.com/8514173/229644164-802cb43e-1aa8-4f19-a329-dd41dfdaf296.mp4



## Structure

```kotlin
- main
-- java
--- com.compose.barcodescanner
---- scanner
----- presentation
------- model
------- BarcodeImageAnalyzer // Set up the Image Processor and add the process flow listener

------- BarcodeResultBoundaryAnalyzer // Based on the BarcodeResult position analyze the position relationship between barcode and scanning frame

```


## BarcodeImageAnalyzer Flow

![BarcodeImageAnalyzer](https://github.com/MARGI3/ComposeBarcodeScanner/blob/6126dc20542865448e6255e12d65eae3bd921e93/recording/barcode_image_analyzer.png)

 At the second step of the diagram listed above, ImageAnalyzer transform the camera captured information into ImageInfo.

 You can see the info below

![Image Log](https://github.com/MARGI3/ComposeBarcodeScanner/blob/6126dc20542865448e6255e12d65eae3bd921e93/recording/log_image_info.png) 



## BarcodeResultBoundaryAnalyzer

|OutOfBoundary|BoundaryOverLap|InsideBoundary|PerfectMatch|
|----|----|----|----|
|![OutOfBoundary](https://github.com/MARGI3/ComposeBarcodeScanner/blob/6126dc20542865448e6255e12d65eae3bd921e93/recording/boundary_relationship_1.png)|![BoundaryOverLap](https://github.com/MARGI3/ComposeBarcodeScanner/blob/6126dc20542865448e6255e12d65eae3bd921e93/recording/boundary_relationship_2.png)|![InsideBoundary](https://github.com/MARGI3/ComposeBarcodeScanner/blob/6126dc20542865448e6255e12d65eae3bd921e93/recording/boundary_relationship_3.png)|![PerfectMatch](https://github.com/MARGI3/ComposeBarcodeScanner/blob/6126dc20542865448e6255e12d65eae3bd921e93/recording/boundary_relationship_4.png)|

## Permission Handling

![Permission](https://github.com/MARGI3/ComposeBarcodeScanner/blob/6126dc20542865448e6255e12d65eae3bd921e93/recording/permission_screenshot.png)
