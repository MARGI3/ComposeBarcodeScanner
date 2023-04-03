package com.compose.barcodescanner.scanner.presentation.model

sealed class BarcodeScannerBottomSheetState {

    data class Loading(
        val barcodeResult: BarCodeResult,
    ) : BarcodeScannerBottomSheetState()

    data class Expanded(
        val barcodeResult: BarCodeResult,
        val information: InformationModel,
    ) : BarcodeScannerBottomSheetState()

    sealed class Error : BarcodeScannerBottomSheetState() {
        object Generic : Error()
    }

    object Hidden : BarcodeScannerBottomSheetState()
}
