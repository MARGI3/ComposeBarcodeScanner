package com.compose.barcodescanner

import androidx.camera.core.ExperimentalGetImage
import com.compose.barcodescanner.scanner.presentation.BarcodeImageAnalyzer
import com.compose.barcodescanner.scanner.presentation.BarcodeResultBoundaryAnalyzer
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

@ExperimentalGetImage
object DependencyProvider {

    fun provideBarcodeScanner(): BarcodeScanner {
        // BarCode format types reference https://en.wikipedia.org/wiki/Barcode
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_CODE_39,
                Barcode.FORMAT_CODE_93,
                Barcode.FORMAT_CODABAR,
                Barcode.FORMAT_DATA_MATRIX,
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_ITF,
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E,
                Barcode.FORMAT_PDF417,
                Barcode.FORMAT_AZTEC,
                Barcode.TYPE_ISBN,
            )
            .build()
        return BarcodeScanning.getClient(options)
    }

    fun provideBarcodeImageAnalyzer(
        barcodeScanner: BarcodeScanner,
    ): BarcodeImageAnalyzer {
        return BarcodeImageAnalyzer(
            barcodeScanner = barcodeScanner
        )
    }

    fun provideBarcodeResultBoundaryAnalyzer(): BarcodeResultBoundaryAnalyzer {
        return BarcodeResultBoundaryAnalyzer()
    }
}