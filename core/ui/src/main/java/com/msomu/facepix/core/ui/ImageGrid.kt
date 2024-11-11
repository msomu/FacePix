package com.msomu.facepix.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.msomu.facepix.core.model.ImageResource

@Composable
fun ImageGrid(
    modifier: Modifier,
    images: List<ImageResource>,
    onImageClick: (ImageResource) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(3), // 3 items per row
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(images) { imageResource ->
            var showOverlay by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.0F)
                    .clickable { onImageClick(imageResource) }
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = imageResource.imagePath,
                        onState = { state ->
                            if (state is AsyncImagePainter.State.Success) {
                                showOverlay = true
                            }
                        }),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                AnimatedVisibility(showOverlay) {
                    ImageGridFaceDetectionOverlay(
                        results = imageResource.faces,
                        imageWidth = imageResource.width,
                        imageHeight = imageResource.height,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}