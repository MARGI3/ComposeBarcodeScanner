package com.compose.barcodescanner.scanner.presentation

import android.Manifest
import android.graphics.RectF
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import com.compose.barcodescanner.R
import com.compose.barcodescanner.scanner.presentation.model.BarcodeScannerBottomSheetState
import com.compose.barcodescanner.scanner.presentation.model.ScanningResult
import com.compose.barcodescanner.ui.theme.ComposeBarcodeScannerTheme
import com.compose.barcodescanner.ui.theme.Purple700
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(
    ExperimentalPermissionsApi::class,
    ExperimentalMaterialApi::class,
)
@Composable
@ExperimentalGetImage
fun BarcodeScannerScreen(
    viewModel: BarcodeScanningViewModel,
    onScreenLoaded: (Boolean) -> Unit = {},
    onPermissionGrantedResult: (Boolean) -> Unit = {},
) {
    val barcodeResult = viewModel.scanningResult.observeAsState(ScanningResult.Initial())
    val freezeCameraPreview = viewModel.freezeCameraPreview.observeAsState(false)
    val resultBottomSheetStateModel =
        viewModel.resultBottomSheetState.observeAsState(BarcodeScannerBottomSheetState.Hidden)

    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA,
        onPermissionResult = {
            onPermissionGrantedResult.invoke(it)
        }
    )

    val resultBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmValueChange = {
            it != ModalBottomSheetValue.HalfExpanded
        }
    )

    LaunchedEffect(key1 = LocalLifecycleOwner.current) {
        val status = cameraPermissionState.status
        if (!status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
        onScreenLoaded.invoke(status.isGranted)
    }

    resultBottomSheetStateModel.value.let {
        LaunchedEffect(key1 = it) {
            when (it) {
                is BarcodeScannerBottomSheetState.Loading -> {
                    resultBottomSheetState.show()
                }
                is BarcodeScannerBottomSheetState.Expanded -> {
                    resultBottomSheetState.show()
                }
                is BarcodeScannerBottomSheetState.Error -> {
                    resultBottomSheetState.show()
                }
                is BarcodeScannerBottomSheetState.Hidden -> {
                    resultBottomSheetState.hide()
                }
            }
        }
    }

    LaunchedEffect(key1 = resultBottomSheetState) {
        snapshotFlow { resultBottomSheetState.isVisible }.collect { visible ->
            viewModel.onBottomSheetDialogStateChanged(visible)
        }
    }

    ComposeBarcodeScannerTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = Purple700
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.onToolbarCloseIconClicked()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                                tint = Color.White,
                            )
                        }

                        Text(
                            modifier = Modifier
                                .weight(1F)
                                .align(Alignment.CenterVertically),
                            text = stringResource(id = R.string.app_name),
                            maxLines = 1,
                            color = Color.White,
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                when (cameraPermissionState.status) {
                    is PermissionStatus.Granted -> {
                        BarcodeScannerContentLayout(
                            imageAnalyzer = viewModel.getBarcodeImageAnalyzer(),
                            onScanningAreaReady = {
                                viewModel.onBarcodeScanningAreaReady(it)
                            },
                            onCameraBoundaryReady = {
                                viewModel.onCameraBoundaryReady(it)
                            },
                            scanningResult = barcodeResult.value,
                            freezeCameraPreview = freezeCameraPreview.value,
                            resultBottomSheetState = resultBottomSheetState,
                            resultBottomSheetStateModel = resultBottomSheetStateModel.value,
                            onBottomSheetCloseIconClicked = { viewModel.onBottomSheetCloseButtonClicked() },
                        )
                    }
                    is PermissionStatus.Denied -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = stringResource(id = R.string.camera_permission_is_denied),
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(
    ExperimentalMaterialApi::class,
)
@ExperimentalGetImage
@Composable
fun BarcodeScannerContentLayout(
    imageAnalyzer: BarcodeImageAnalyzer,
    scanningResult: ScanningResult,
    onScanningAreaReady: (RectF) -> Unit,
    onCameraBoundaryReady: (RectF) -> Unit,
    freezeCameraPreview: Boolean,
    resultBottomSheetState: ModalBottomSheetState,
    resultBottomSheetStateModel: BarcodeScannerBottomSheetState,
    onBottomSheetCloseIconClicked: () -> Unit = {},
) {
    val screenWidth = remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { layoutCoordinates ->
                screenWidth.value = layoutCoordinates.size.width
            }
    ) {
        BarcodeScannerLayout(
            imageAnalyzer = imageAnalyzer,
            onScanningAreaReady = onScanningAreaReady,
            onCameraBoundaryReady = onCameraBoundaryReady,
            scanningResult = scanningResult,
            freezeCameraPreview = freezeCameraPreview,
        )

        BarcodeScannerBottomSheetLayout(
            resultBottomSheetState = resultBottomSheetState,
            resultBottomSheetStateModel = resultBottomSheetStateModel,
            onCloseIconClicked = onBottomSheetCloseIconClicked,
        )
    }
}
