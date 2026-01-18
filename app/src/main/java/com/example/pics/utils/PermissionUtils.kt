package com.example.pics.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Checks whether all required CameraX permissions are granted.
 */
fun hasRequiredPermissions(context: Context): Boolean {
    return CAMERAX_PERMISSIONS.all { permission ->
        ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}

/**
 * Requests CameraX permissions from the user.
 *
 * This should be called only when permissions are not already granted.
 */
fun requestCameraPermissions(activity: Activity) {
    ActivityCompat.requestPermissions(
        activity,
        CAMERAX_PERMISSIONS,
        CAMERA_PERMISSION_REQUEST_CODE
    )
}

/**
 * List of permissions required for camera and video recording.
 */
private val CAMERAX_PERMISSIONS = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
    arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.READ_MEDIA_IMAGES
    )
} else {
    arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )
}

private const val CAMERA_PERMISSION_REQUEST_CODE = 1001

// TODO (GOOD FIRST ISSUE):
// Show a permission rationale dialog before requesting permissions
