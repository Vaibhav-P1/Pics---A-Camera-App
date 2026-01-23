package com.example.pics.viewmodel

import android.app.Application
import android.content.ContentUris
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {
    data class Media(
        val uri: Uri,
        val isVideo: Boolean,
        val dateAdded: Long
    )

    private val _photos = MutableStateFlow<List<Media>>(emptyList())
    val photos = _photos.asStateFlow()

    private val _videos = MutableStateFlow<List<Media>>(emptyList())
    val videos = _videos.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused = _isPaused.asStateFlow()

    init {
        loadMedia()
    }

    private fun loadMedia() {
        viewModelScope.launch {
            val loadedPhotos = mutableListOf<Media>()
            val loadedVideos = mutableListOf<Media>()

            withContext(Dispatchers.IO) {
                // Load Photos
                val projection = arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATE_ADDED
                )
                val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
                val selectionArgs = arrayOf("%Pictures/Pics%")
                val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

                getApplication<Application>().contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val date = cursor.getLong(dateColumn)
                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        loadedPhotos.add(Media(contentUri, false, date))
                    }
                }

                // Load Videos
                val videoProjection = arrayOf(
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DATE_ADDED
                )
                val videoSelection = "${MediaStore.Video.Media.RELATIVE_PATH} LIKE ?"
                val videoSelectionArgs = arrayOf("%Movies/Pics%")
                val videoSortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

                getApplication<Application>().contentResolver.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    videoProjection,
                    videoSelection,
                    videoSelectionArgs,
                    videoSortOrder
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                    val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val date = cursor.getLong(dateColumn)
                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        loadedVideos.add(Media(contentUri, true, date))
                    }
                }
            }

            _photos.value = loadedPhotos
            _videos.value = loadedVideos
        }
    }

    fun onTakePhoto(uri: Uri) {
        val newMedia = Media(uri, false, System.currentTimeMillis() / 1000)
        _photos.value = listOf(newMedia) + _photos.value
    }

    fun onVideoRecorded(uri: Uri) {
        val newMedia = Media(uri, true, System.currentTimeMillis() / 1000)
        _videos.value = listOf(newMedia) + _videos.value
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
