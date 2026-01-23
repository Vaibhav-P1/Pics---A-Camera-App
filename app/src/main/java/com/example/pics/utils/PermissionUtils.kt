package com.example.pics.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Checks whether all required CameraX and Media permissions are granted.
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
 * Requests CameraX and Media permissions from the user.
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
 * List of permissions required for camera, video recording, and gallery access.
 */
private val CAMERAX_PERMISSIONS = mutableListOf(
    Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO
).apply {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        add(Manifest.permission.READ_MEDIA_IMAGES)
        add(Manifest.permission.READ_MEDIA_VIDEO)
    } else {
        add(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}.toTypedArray()

private const val CAMERA_PERMISSION_REQUEST_CODE = 1001

// TODO (GOOD FIRST ISSUE):
// Show a permission rationale dialog before requesting permissions
