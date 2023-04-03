package com.compose.barcodescanner.scanner.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.compose.barcodescanner.R
import com.compose.barcodescanner.scanner.presentation.model.BarCodeResult
import com.compose.barcodescanner.scanner.presentation.model.BarcodeScannerBottomSheetState
import com.compose.barcodescanner.scanner.presentation.model.InformationModel
import com.compose.barcodescanner.ui.theme.Purple200

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BarcodeScannerBottomSheetLayout(
    resultBottomSheetState: ModalBottomSheetState,
    resultBottomSheetStateModel: BarcodeScannerBottomSheetState,
    onCloseIconClicked: () -> Unit = {},
) {
    ModalBottomSheetLayout(
        sheetState = resultBottomSheetState,
        sheetContent = {
            when (resultBottomSheetStateModel) {
                is BarcodeScannerBottomSheetState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Purple200
                        )
                    }
                }
                is BarcodeScannerBottomSheetState.Expanded -> {
                    BarcodeScannerInformationLayout(
                        barCodeResult = resultBottomSheetStateModel.barcodeResult,
                        informationModel = resultBottomSheetStateModel.information,
                        onCloseIconClicked = onCloseIconClicked,
                    )
                }
                is BarcodeScannerBottomSheetState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(24.dp)
                    ) {
                        BottomSheetHeaderLayout(onCloseIconClicked = onCloseIconClicked)
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = stringResource(id = R.string.loading_product_result_error_generic),
                            )
                        }
                    }
                }
                is BarcodeScannerBottomSheetState.Hidden -> {
                    // bottom sheet content should have real content at any case. otherwise it will throw exception
                    Text(text = "")
                }
            }
        },
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        content = {}
    )
}


@Composable
fun BottomSheetHeaderLayout(
    onCloseIconClicked: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Image(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            painter = painterResource(id = R.drawable.ic_bottom_sheet_top_drag_handle),
            contentDescription = "Bottom Sheet Drag Handle"
        )

        Image(
            modifier = Modifier
                .align(Alignment.End)
                .clickable(onClick = onCloseIconClicked),
            painter = painterResource(id = R.drawable.ic_close_black),
            contentDescription = "Bottom Sheet Close Icon"
        )
    }
}

@Composable
fun BarcodeScannerInformationLayout(
    barCodeResult: BarCodeResult,
    informationModel: InformationModel,
    onCloseIconClicked: () -> Unit = {},
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp)
    ) {
        BottomSheetHeaderLayout(onCloseIconClicked = onCloseIconClicked)

        Text(
            modifier = Modifier.padding(24.dp),
            text = informationModel.propertyOne,
            textAlign = TextAlign.Center,
        )

        Text(
            modifier = Modifier.padding(24.dp),
            text = "barcodeResult - ${barCodeResult.barCode.displayValue}",
            textAlign = TextAlign.Center,
        )
    }
}
