package com.example.pics.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class MainViewModel : ViewModel() {
    private val _photos = MutableStateFlow<List<File>>(emptyList())
    val photos = _photos.asStateFlow()

    private val _videos = MutableStateFlow<List<File>>(emptyList())
    val videos = _videos.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused = _isPaused.asStateFlow()

    fun onTakePhoto(file: File) {
        _photos.value = _photos.value + file
    }

    fun onVideoRecorded(file: File) {
        _videos.value = _videos.value + file
    }

    fun setRecording(recording: Boolean) {
        _isRecording.value = recording
        if (!recording) {
            _isPaused.value = false
        }
    }

    fun setPaused(paused: Boolean) {
        _isPaused.value = paused
    }

    // TODO: Persist images using Room or File storage
}
