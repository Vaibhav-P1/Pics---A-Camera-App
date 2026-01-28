package com.example.pics.ui

import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.pics.camera.CameraActions
import com.example.pics.camera.CameraPreview
import com.example.pics.utils.hasRequiredPermissions
import com.example.pics.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import android.annotation.SuppressLint


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    controller: LifecycleCameraController,
    viewModel: MainViewModel,
    isRecording: Boolean,
    isPaused: Boolean,
    onRecordingToggle: () -> Unit,
    onPauseResumeToggle: () -> Unit,
    onOpenFullGallery: () -> Unit
) {
    val photos by viewModel.photos.collectAsState()
    val videos by viewModel.videos.collectAsState()
    val recordingTime by viewModel.recordingTime.collectAsState()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val infiniteTransition = rememberInfiniteTransition(label = "recording")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            PhotoBottomSheet(photos, videos)
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // 📷 Camera Preview
            CameraPreview(
                controller = controller,
                modifier = Modifier.fillMaxSize()
            )

            // 🔴 Recording Indicator & Timer
            if (isRecording) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 48.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .alpha(if (isPaused) 1f else alpha)
                            .background(if (isPaused) Color.Yellow else Color.Red, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPaused) "Paused (${formatTime(recordingTime)})" else formatTime(recordingTime),
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }

            // 🔄 Switch Camera
            IconButton(
                onClick = {
                    controller.cameraSelector =
                        if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        else
                            CameraSelector.DEFAULT_BACK_CAMERA
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Switch Camera"
                )
            }

            // 🔘 Bottom Controls
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // 📂 Open Full Gallery
                IconButton(
                    onClick = onOpenFullGallery
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Open Full Gallery"
                    )
                }

                // 📸 Take Photo
                IconButton(
                    onClick = {
                        CameraActions.takePhoto(
                            context = context,
                            controller = controller,
                            onPhotoTaken = viewModel::onTakePhoto
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Take Photo"
                    )
                }

                // 🎥 Record Video / Stop
                @SuppressLint("MissingPermission")
                IconButton(
                    onClick = {
                        if (hasRequiredPermissions(context)) {
                            onRecordingToggle()
                        } else {
                            Toast.makeText(
                                context,
                                "Microphone permission required",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    Icon(
                        // 🔄 Dynamic Icon: Stop (Square) when recording, Camera (Video) when stopped
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Videocam,
                        contentDescription = if (isRecording) "Stop Recording" else "Record Video",
                        // 🎨 Dynamic Color: Red when recording, White when stopped
                        tint = if (isRecording) Color.Red else Color.White
                    )
                }

                // ⏸️ Pause / Resume Video (Only visible when recording)
                if (isRecording) {
                    IconButton(
                        onClick = onPauseResumeToggle
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (isPaused) "Resume Recording" else "Pause Recording",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(seconds: Long): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}