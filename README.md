Jetpack Compose Barcode Scanner


[Jetpack Compose](https://developer.android.com/jetpack/compose) + [Google ML Kit - Barcode Scanning](https://developers.google.com/ml-kit/vision/barcode-scanning)


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



