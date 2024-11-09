package com.msomu.facepix.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.msomu.facepix.model.ImageResource


@Composable
fun ImageGrid(modifier: Modifier, images: List<ImageResource>) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(3), // 3 items per row
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(images) { imageResource ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.0F)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageResource.imagePath),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                FaceDetectionOverlay(
                    results = imageResource.faces,
                    imageWidth = imageResource.width,
                    imageHeight = imageResource.height,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}