package com.compose.barcodescanner.scanner.presentation

import android.graphics.RectF
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.viewModelFactory
import com.compose.barcodescanner.DependencyProvider
import com.compose.barcodescanner.SingleLiveEvent
import com.compose.barcodescanner.scanner.presentation.model.BarCodeResult
import com.compose.barcodescanner.scanner.presentation.model.BarcodeScannerBottomSheetState
import com.compose.barcodescanner.scanner.presentation.model.InformationModel
import com.compose.barcodescanner.scanner.presentation.model.ScanningResult
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalGetImage
class BarcodeScanningViewModel constructor(
    private val barcodeScanner: BarcodeScanner,
    private val barcodeImageAnalyzer: BarcodeImageAnalyzer = DependencyProvider.provideBarcodeImageAnalyzer(barcodeScanner),
    private val barcodeResultBoundaryAnalyzer: BarcodeResultBoundaryAnalyzer = DependencyProvider.provideBarcodeResultBoundaryAnalyzer(),
) : ViewModel(), DefaultLifecycleObserver {

    private val _scanningResult = MutableLiveData<ScanningResult>()
    val scanningResult: LiveData<ScanningResult> = _scanningResult

    private val _popBackStack = SingleLiveEvent<Unit>()
    val popBackStack: LiveData<Unit> = _popBackStack

    private val _freezeCameraPreview = SingleLiveEvent<Boolean>()
    val freezeCameraPreview: LiveData<Boolean> = _freezeCameraPreview

    private val _resultBottomSheetState =
        MutableLiveData<BarcodeScannerBottomSheetState>()
    val resultBottomSheetState: LiveData<BarcodeScannerBottomSheetState> =
        _resultBottomSheetState

    private var loadingProductCode: String = ""
    private var isResultBottomSheetShowing = false
    private lateinit var barcodeResult: BarCodeResult

    init {
        setupImageAnalyzer()
    }

    fun onBackButtonClicked() {
        if (isResultBottomSheetShowing) {
            _resultBottomSheetState.value = BarcodeScannerBottomSheetState.Hidden
        } else {
            _popBackStack.call()
        }
    }

    fun onBarcodeScanningAreaReady(scanningArea: RectF) {
        barcodeResultBoundaryAnalyzer.onBarcodeScanningAreaReady(scanningArea)
    }

    fun onCameraBoundaryReady(cameraBoundary: RectF) {
        barcodeResultBoundaryAnalyzer.onCameraBoundaryReady(cameraBoundary)
    }

    fun onBottomSheetCloseButtonClicked() {
        _resultBottomSheetState.value = BarcodeScannerBottomSheetState.Hidden
    }

    fun getBarcodeImageAnalyzer(): BarcodeImageAnalyzer {
        return barcodeImageAnalyzer
    }

    fun onToolbarCloseIconClicked() {
        _popBackStack.call()
    }

    fun onBottomSheetDialogStateChanged(expanded: Boolean) {
        isResultBottomSheetShowing = when {
            expanded -> {
                true
            }
            else -> {
                // after bottom sheet hidden, resume camera preview
                freezeCameraPreview(false)
                false
            }
        }
    }

    private fun setupImageAnalyzer() {
        barcodeImageAnalyzer.setProcessListener(
            listener = object : BarcodeImageAnalyzer.ProcessListenerAdapter() {
                override fun onSucceed(results: List<Barcode>, inputImage: InputImage) {
                    super.onSucceed(results, inputImage)
                    handleBarcodeResults(results, inputImage)
                }
            }
        )
    }

    private fun handleBarcodeResults(results: List<Barcode>, inputImage: InputImage) {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, exception ->
                notifyErrorResult(exception)
                Log.e("TAG", "$exception")
            }
        ) {
            if (isResultBottomSheetShowing) {
                // skip the analyzer process if the result bottom sheet is showing
                return@launch
            }

            val scanningResult = barcodeResultBoundaryAnalyzer.analyze(results, inputImage)
            _scanningResult.value = scanningResult
            if (scanningResult is ScanningResult.PerfectMatch) {
                loadProductDetailsWithBarcodeResult(scanningResult)
            }
        }
    }

    private fun notifyErrorResult(exception: Throwable) {
        _resultBottomSheetState.value =
            BarcodeScannerBottomSheetState.Error.Generic
    }

    private suspend fun loadProductDetailsWithBarcodeResult(scanningResult: ScanningResult.PerfectMatch) {
        val productCode = scanningResult.barCodeResult.barCode.displayValue
        if (productCode != null) {
            loadingProductCode = productCode
            showBottomSheetLoading(scanningResult.barCodeResult)
            freezeCameraPreview(true)
            // mock API call to fetch information with barcode result
            delay(2000)
            bindBottomSheetResultInformation(barcodeResult = scanningResult.barCodeResult)
        } else {
            // Show Error Information
        }
    }

    private fun bindBottomSheetResultInformation(barcodeResult: BarCodeResult) {
        _resultBottomSheetState.value = BarcodeScannerBottomSheetState.Expanded(
            barcodeResult = barcodeResult,
            information = InformationModel(
                propertyOne = "This is mock information fetched from server",
                propertyTwo = "This is mock information fetched from server"
            )
        )
        this.barcodeResult = barcodeResult
    }

    private fun showBottomSheetLoading(
        barcodeResult: BarCodeResult,
    ) {
        isResultBottomSheetShowing = true
        _resultBottomSheetState.value = BarcodeScannerBottomSheetState.Loading(barcodeResult)
        this.barcodeResult = barcodeResult
    }

    private fun freezeCameraPreview(freeze: Boolean) {
        // true - freeze the camera preview,
        // false - resume the camera preview
        _freezeCameraPreview.value = freeze
    }
}
