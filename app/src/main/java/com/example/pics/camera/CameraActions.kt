package com.example.pics.camera

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

object CameraActions {
    private const val TAG = "CameraActions"
    var recording: Recording? = null

    fun takePhoto(
        context: Context,
        controller: LifecycleCameraController,
        onPhotoTaken: (Uri) -> Unit
    ) {
        val name = "IMG_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Pics")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        controller.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    outputFileResults.savedUri?.let { onPhotoTaken(it) }
                    Toast.makeText(context, "Photo saved to gallery", Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Failed to save photo", exception)
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
        onRecordingFinished: (Uri?) -> Unit
    ) {
        if (recording != null) {
            recording?.stop()
            recording = null
            return
        }

        val name = "VID_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/Pics")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(context.contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()

        onRecordingStarted()
        
        recording = controller.startRecording(
            mediaStoreOutputOptions,
            AudioConfig.create(false),
            ContextCompat.getMainExecutor(context)
        ) { event ->
            if (event is VideoRecordEvent.Finalize) {
                val uri = if (!event.hasError()) event.outputResults.outputUri else null
                onRecordingFinished(uri)
                
                if (event.hasError()) {
                    Log.e(TAG, "Video recording failed: Error code ${event.error}")
                    Toast.makeText(context, "Recording failed", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Video saved to gallery", Toast.LENGTH_SHORT).show()
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