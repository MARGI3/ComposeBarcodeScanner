package com.compose.barcodescanner.scanner.presentation

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@ExperimentalGetImage
class BarcodeImageAnalyzer constructor(
    private val barcodeScanner: BarcodeScanner
) : ImageAnalysis.Analyzer {

    private val executor = Executors.newSingleThreadExecutor()
    private var processing = false
    private var processListener: ProcessListener? = null

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return
        if (processing) return

        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        processing = true
        barcodeScanner.process(inputImage)
            .addOnSuccessListener { results ->
                processListener?.onSucceed(results, inputImage)
            }
            .addOnCanceledListener {
                processListener?.onCanceled()
            }
            .addOnFailureListener {
                processListener?.onFailed(it)
            }
            .addOnCompleteListener {
                imageProxy.close()
                processListener?.onCompleted()
                processing = false
            }
    }

    fun setProcessListener(listener: ProcessListener) {
        processListener = listener
    }

    fun getAnalyzerExecutor(): Executor = executor

    interface ProcessListener {
        fun onSucceed(results: List<Barcode>, inputImage: InputImage)
        fun onCanceled()
        fun onCompleted()
        fun onFailed(exception: Exception)
    }

    abstract class ProcessListenerAdapter : ProcessListener {
        override fun onSucceed(results: List<Barcode>, inputImage: InputImage) {}

        override fun onCanceled() {}

        override fun onCompleted() {}

        override fun onFailed(exception: Exception) {}
    }
}
