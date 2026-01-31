package com.example.pics.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import com.example.pics.viewmodel.MainViewModel
import java.io.File

@Composable
fun PhotoBottomSheet(
    photos: List<MainViewModel.Media>,
    videos: List<MainViewModel.Media>,
    isVisible: Boolean = true
) {
    var selectedMedia by remember { mutableStateOf<MainViewModel.Media?>(null) }
    val context = LocalContext.current
    
    // Animation trigger when sheet becomes visible
    var animationTriggered by remember { mutableStateOf(false) }
    LaunchedEffect(isVisible) {
        if (isVisible) {
            animationTriggered = true
        }
    }
    
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .build()
    }

    // Full-screen Photo Dialog
    selectedMedia?.let { media ->
        Dialog(
            onDismissRequest = { selectedMedia = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = media.uri,
                    imageLoader = imageLoader,
                    contentDescription = "Full Screen Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                IconButton(
                    onClick = { selectedMedia = null },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        }
    }

    if (photos.isEmpty() && videos.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No photos or videos yet. Capture some!")
        }
    } else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalItemSpacing = 12.dp,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Show Photos with staggered animation
            itemsIndexed(photos) { index, media ->
                AnimatedMediaItem(
                    index = index,
                    isVisible = animationTriggered
                ) {
                    AsyncImage(
                        model = media.uri,
                        imageLoader = imageLoader,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray)
                            .clickable { selectedMedia = media },
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Show Videos with staggered animation
            itemsIndexed(videos) { index, media ->
                AnimatedMediaItem(
                    index = photos.size + index, // Offset by photos count for continuous stagger
                    isVisible = animationTriggered
                ) {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.DarkGray)
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(media.uri, "video/mp4")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = media.uri,
                            imageLoader = imageLoader,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        
                        // Dark overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f))
                        )
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                shape = CircleShape,
                                color = Color.Black.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = "Video",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Video",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Animated wrapper for media items with staggered fade-in and scale animation
 */
@Composable
private fun AnimatedMediaItem(
    index: Int,
    isVisible: Boolean,
    content: @Composable () -> Unit
) {
    val animatedProgress = remember { Animatable(0f) }
    
    LaunchedEffect(isVisible) {
        if (isVisible) {
            // Staggered delay based on index (50ms per item, max 500ms)
            val delay = (index * 50).coerceAtMost(500)
            kotlinx.coroutines.delay(delay.toLong())
            animatedProgress.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }
    
    Box(
        modifier = Modifier
            .graphicsLayer {
                alpha = animatedProgress.value
                scaleX = 0.8f + (0.2f * animatedProgress.value)
                scaleY = 0.8f + (0.2f * animatedProgress.value)
                translationY = (1f - animatedProgress.value) * 50f
            }
    ) {
        content()
    }
}
