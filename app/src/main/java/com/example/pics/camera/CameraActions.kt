package com.example.pics.camera



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
        onPhotoTaken: (File) -> Unit
    ) {
        val outputFile = File(
            context.filesDir,
            "photo_${System.currentTimeMillis()}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

        controller.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onPhotoTaken(outputFile)
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
        onRecordingFinished: (File?) -> Unit
    ) {
        if (recording != null) {
            recording?.stop()
            recording = null
            return
        }

        val outputFile = File(
            context.filesDir,
            "video_${System.currentTimeMillis()}.mp4"
        )

        onRecordingStarted()
        
        // Try recording without audio if audio fails, or just disable it for now on emulator
        // For debugging purposes, we'll try with audio first
        recording = controller.startRecording(
            FileOutputOptions.Builder(outputFile).build(),
            AudioConfig.create(false), // Disabled audio for emulator compatibility
            ContextCompat.getMainExecutor(context)
        ) { event ->
            if (event is VideoRecordEvent.Finalize) {
                val file = if (!event.hasError()) outputFile else null
                onRecordingFinished(file)
                
                if (event.hasError()) {
                    Log.e(TAG, "Video recording failed: Error code ${event.error}")
                    
                    // If it failed with NO_VALID_DATA, it might be the audio source on emulator
                    if (event.error == VideoRecordEvent.Finalize.ERROR_NO_VALID_DATA) {
                        Log.w(TAG, "No valid data received. This is common on emulators with audio enabled. Retrying without audio...")
                        // We could automatically retry here, but for now let's just inform the user
                    }

                    val errorMsg = when (event.error) {
                        VideoRecordEvent.Finalize.ERROR_INSUFFICIENT_STORAGE -> "Insufficient storage"
                        VideoRecordEvent.Finalize.ERROR_FILE_SIZE_LIMIT_REACHED -> "File size limit reached"
                        VideoRecordEvent.Finalize.ERROR_NO_VALID_DATA -> "No data (Mic issue?)"
                        VideoRecordEvent.Finalize.ERROR_INVALID_OUTPUT_OPTIONS -> "Invalid output options"
                        VideoRecordEvent.Finalize.ERROR_ENCODING_FAILED -> "Encoding failed"
                        VideoRecordEvent.Finalize.ERROR_RECORDER_ERROR -> "Recorder error"
                        else -> "Unknown error: ${event.error}"
                    }
                    Toast.makeText(context, "Recording failed: $errorMsg", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Recording saved", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun pauseVideoRecording() {
        recording?.pause()
    }

    fun resumeVideoRecording() {
        recording?.resume()
    }

    // TODO (GOOD FIRST ISSUE):
    // Add flash toggle support
}
