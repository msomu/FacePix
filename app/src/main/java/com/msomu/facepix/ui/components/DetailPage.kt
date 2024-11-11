package com.msomu.facepix.ui.components

import android.graphics.RectF
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.msomu.facepix.database.model.PersonEntity
import com.msomu.facepix.model.Face
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageDetailScreen(
    uiState: ImageDetailUiState,
    availablePersons: List<PersonEntity>,
    modifier: Modifier = Modifier,
    tagFace: (RectF, Long, Float) -> Unit,
    addPerson: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedFace by remember { mutableStateOf<Face?>(null) }
    var showPersonDialog by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Image Detail") }, navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        })
    }) { padding ->
        when (val state = uiState) {
            is ImageDetailUiState.Loading -> {
                Box(modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }

            is ImageDetailUiState.Success -> {
                Column(
                    modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    var showOverlay by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = state.image.imagePath,
                                onState = { state ->
                                    Timber.tag("msomu").d("DetailPage: $state")
                                    if (state is AsyncImagePainter.State.Success) {
                                        showOverlay = true
                                    }
                                }),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        androidx.compose.animation.AnimatedVisibility(showOverlay) {
                            FaceDetectionOverlay(
                                results = state.image.detectedFaces,
                                imageWidth = state.image.width,
                                imageHeight = state.image.height,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            is ImageDetailUiState.Error -> {
                Text(state.message)
            }
        }
    }
}