package com.example.pics.camera



import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
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
import java.text.SimpleDateFormat
import java.util.Locale

object CameraActions {

    var recording: Recording? = null

    fun takePhoto(
        context: Context,
        controller: LifecycleCameraController,
        onPhotoSaved: () -> Unit
    ) {
        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        controller.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Toast.makeText(context, "Photo saved: ${output.savedUri}", Toast.LENGTH_SHORT).show()
                    onPhotoSaved()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(context, "Failed to capture photo: ${exception.message}", Toast.LENGTH_SHORT).show()
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

        val outputFile = File(
            context.filesDir,
            "video_${System.currentTimeMillis()}.mp4"
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
