package com.example.pics.viewmodel

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainViewModel : ViewModel() {

    private val _photoUris = MutableStateFlow<List<Uri>>(emptyList())
    val photoUris = _photoUris.asStateFlow()

    private val _videoFiles = MutableStateFlow<List<File>>(emptyList())
    val videoFiles = _videoFiles.asStateFlow()

    fun loadMedia(context: Context) {
        loadPhotos(context.contentResolver)
        loadVideos(context)
    }

    private fun loadPhotos(contentResolver: ContentResolver) {
        viewModelScope.launch {
            val loadedUris = withContext(Dispatchers.IO) {
                val list = mutableListOf<Uri>()
                val projection = arrayOf(
                    MediaStore.Images.Media._ID
                )
                val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

                contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    sortOrder
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        list.add(contentUri)
                    }
                }
                list
            }
            _photoUris.value = loadedUris
        }
    }

    private fun loadVideos(context: Context) {
        viewModelScope.launch {
            val videos = withContext(Dispatchers.IO) {
                context.filesDir.listFiles { file ->
                    file.extension == "mp4"
                }?.toList() ?: emptyList()
            }
            _videoFiles.value = videos.sortedByDescending { it.lastModified() }
        }
    }
}
