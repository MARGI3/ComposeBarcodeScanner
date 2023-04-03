package com.compose.barcodescanner.scanner.presentation.model

import android.graphics.RectF
import androidx.annotation.StringRes
import com.compose.barcodescanner.R
import com.google.mlkit.vision.barcode.common.Barcode

sealed class ScanningResult(
    @StringRes
    open val instructionResId: Int,
    open val barCodeResult: BarCodeResult? = null,
) {
    class Initial(
        override val instructionResId: Int = R.string.point_your_camera_at_a_barcode
    ) : ScanningResult(instructionResId)

    class OutOfBoundary(
        override val instructionResId: Int = R.string.point_your_camera_at_a_barcode
    ) : ScanningResult(instructionResId)

    class BoundaryOverLap(
        override val instructionResId: Int = R.string.move_your_camera_to_place_the_barcode_in_the_frame,
        override val barCodeResult: BarCodeResult,
    ) : ScanningResult(instructionResId, barCodeResult)

    class InsideBoundary(
        override val instructionResId: Int = R.string.move_closer_to_the_barcode,
        override val barCodeResult: BarCodeResult,
    ) : ScanningResult(instructionResId, barCodeResult)

    class PerfectMatch(
        override val instructionResId: Int = R.string.loading_the_result,
        override val barCodeResult: BarCodeResult,
    ) : ScanningResult(instructionResId, barCodeResult)
}

data class BarCodeResult(
    val barCode: Barcode,
    val globalPosition: RectF, // transformed position of the barcode rectangle
)
