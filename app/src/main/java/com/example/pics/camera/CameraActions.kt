package com.example.pics.camera



import android.Manifest
import android.content.Context
import android.graphics.Bitmap
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
        controller: LifecycleCameraController
    ) {
        if (recording != null) {
            recording?.stop()
            recording = null
            return
        }

        val format = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault())
        val timestamp = format.format(java.util.Date())
        
        val outputFile = File(
            context.filesDir,
            "VID_$timestamp.mp4"
        )

        recording = controller.startRecording(
            FileOutputOptions.Builder(outputFile).build(),
            AudioConfig.create(true),
            ContextCompat.getMainExecutor(context)
        ) { event ->
            if (event is VideoRecordEvent.Finalize) {
                Toast.makeText(
                    context,
                    if (event.hasError()) "Recording failed" else "Recording saved",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // TODO (GOOD FIRST ISSUE):
    // Add flash toggle support
}
