package com.example.pics.camera

import java.text.SimpleDateFormat
import java.util.Locale
import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.core.content.ContextCompat
import java.io.File

object CameraActions {
    private const val TAG = "CameraActions"
    var recording: Recording? = null

    fun takePhoto(
        context: Context,
        controller: LifecycleCameraController,
        onPhotoTaken: (Bitmap) -> Unit
    ) {
        controller.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    onPhotoTaken(image.toBitmap())
                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(context, "Failed to capture photo", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun toggleVideoRecording(
        context: Context,
        controller: LifecycleCameraController,
        onRecordingStarted: () -> Unit,
        onRecordingFinished: (File?) -> Unit
    ) {
        if (recording != null) {
            recording?.stop()
            recording = null
            return
        }

        // 1. Correct Naming Convention
        val fileName = "VID_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".mp4"
        val outputFile = File(
            context.filesDir,
            fileName
        )

        onRecordingStarted()
        
        recording = controller.startRecording(
            FileOutputOptions.Builder(outputFile).build(),
            AudioConfig.create(false),
            ContextCompat.getMainExecutor(context)
        ) { event ->
            if (event is VideoRecordEvent.Finalize) {
                val file = if (!event.hasError()) outputFile else null
                onRecordingFinished(file)
                
                if (event.hasError()) {
                    Log.e(TAG, "Video recording failed: Error code ${event.error}")
                    Toast.makeText(context, "Recording failed", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Recording saved", Toast.LENGTH_SHORT).show()
                }
            }
        }
    } // <--- This brace was missing!

    fun pauseVideoRecording() {
        recording?.pause()
    }

    fun resumeVideoRecording() {
        recording?.resume()
    }

    // TODO (GOOD FIRST ISSUE):
    // Add flash toggle support
}