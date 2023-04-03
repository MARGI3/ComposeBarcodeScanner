package com.compose.barcodescanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.camera.core.ExperimentalGetImage
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.compose.barcodescanner.scanner.presentation.BarcodeScannerScreen
import com.compose.barcodescanner.scanner.presentation.BarcodeScanningViewModel

@ExperimentalGetImage
class MainActivity : ComponentActivity() {

    private val barcodeScanner = DependencyProvider.provideBarcodeScanner()

    private val viewModel by viewModels<BarcodeScanningViewModel>(
        factoryProducer = {
            viewModelFactory {
                initializer {
                    BarcodeScanningViewModel(
                        barcodeScanner = barcodeScanner,
                        barcodeImageAnalyzer = DependencyProvider.provideBarcodeImageAnalyzer(
                            barcodeScanner
                        ),
                        barcodeResultBoundaryAnalyzer = DependencyProvider.provideBarcodeResultBoundaryAnalyzer()
                    )
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BarcodeScannerScreen(viewModel = viewModel)
        }
        setupBackButtonCallback()
        observeOnViewModel()
    }

    private fun setupBackButtonCallback() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.onBackButtonClicked()
            }
        })
    }

    private fun observeOnViewModel() {
        viewModel.popBackStack.observe(this) {
            this.finish()
        }
    }
}