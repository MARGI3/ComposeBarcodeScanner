package com.compose.barcodescanner

import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun Context.getCameraXProvider(timeOutLong: Long = 5000): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener(
            {
                continuation.resume(future.get(timeOutLong, TimeUnit.MILLISECONDS))
            },
            ContextCompat.getMainExecutor(this)
        )
    }
}