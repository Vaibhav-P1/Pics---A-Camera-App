package com.example.pics.ui

import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pics.camera.CameraActions
import com.example.pics.camera.CameraPreview
import com.example.pics.utils.hasRequiredPermissions
import com.example.pics.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    controller: LifecycleCameraController,
    viewModel: MainViewModel,
    isRecording: Boolean,
    isPaused: Boolean,
    onRecordingToggle: () -> Unit,
    onPauseResumeToggle: () -> Unit
) {
    val bitmaps by viewModel.bitmaps.collectAsState()
    val videos by viewModel.videos.collectAsState()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // REMOVED local isRecording variable because it is now passed as a parameter above

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            PhotoBottomSheet(bitmaps, videos)
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

            // 🔴 Recording Indicator (From Owner's Update)
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
                            .background(if (isPaused) Color.Yellow else Color.Red, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPaused) "Paused" else "Recording",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge
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

                // 📂 Open Gallery (Bottom Sheet)
                IconButton(
                    onClick = {
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = "Open Gallery"
                    )
                }

                // 📸 Take Photo
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(90.dp) 
                        .border(4.dp, Color.White, CircleShape) 
                        .padding(10.dp) 
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.8f)) 
                        .clickable { 
                            CameraActions.takePhoto(
                                context = context,
                                controller = controller,
                                onPhotoTaken = viewModel::onTakePhoto
                            )
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Take Photo",
                        tint = Color.Black,
                        modifier = Modifier.size(40.dp)
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
                        imageVector = Icons.Default.Videocam, // CHANGED: Always show Camera icon
                        contentDescription = "Record Video",
                        tint = Color.White
                    )

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