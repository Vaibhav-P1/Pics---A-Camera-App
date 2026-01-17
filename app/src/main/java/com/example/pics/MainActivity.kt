package com.example.pics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.LifecycleCameraController
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pics.camera.CameraActions
import com.example.pics.ui.CameraScreen
import com.example.pics.ui.theme.PicsTheme
import com.example.pics.utils.hasRequiredPermissions
import com.example.pics.utils.requestCameraPermissions
import com.example.pics.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        if (!hasRequiredPermissions(this)) {
            requestCameraPermissions(this)
        }

        setContent {
            PicsTheme {
                val cameraController = remember {
                    LifecycleCameraController(applicationContext).apply {
                        setEnabledUseCases(
                            LifecycleCameraController.IMAGE_CAPTURE or
                                    LifecycleCameraController.VIDEO_CAPTURE
                        )
                    }
                }

                val viewModel: MainViewModel = viewModel()
                val isRecording by viewModel.isRecording.collectAsState()
                val isPaused by viewModel.isPaused.collectAsState()

                CameraScreen(
                    controller = cameraController,
                    viewModel = viewModel,
                    isRecording = isRecording,
                    isPaused = isPaused,
                    onRecordingToggle = {
                        CameraActions.toggleVideoRecording(
                            context = this,
                            controller = cameraController,
                            onRecordingStarted = { viewModel.setRecording(true) },
                            onRecordingFinished = { file ->
                                viewModel.setRecording(false)
                                if (file != null) {
                                    viewModel.onVideoRecorded(file)
                                }
                            }
                        )
                    },
                    onPauseResumeToggle = {
                        if (isPaused) {
                            CameraActions.resumeVideoRecording()
                            viewModel.setPaused(false)
                        } else {
                            CameraActions.pauseVideoRecording()
                            viewModel.setPaused(true)
                        }
                    }
                )
            }
        }
    }
}
