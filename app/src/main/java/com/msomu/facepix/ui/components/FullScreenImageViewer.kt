package com.msomu.facepix.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.msomu.facepix.model.Face
import com.msomu.facepix.model.ImageResource

@Composable
fun FullScreenImageViewer(
    imageResource: ImageResource,
    onDismiss: () -> Unit,
    onTagFace: (Face, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFace by remember { mutableStateOf<Face?>(null) }
    var showTagDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        // Full screen image with faces overlay
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = rememberAsyncImagePainter(model = imageResource.imagePath),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            // Clickable face boxes
            imageResource.faces.forEach { face ->
                FaceBox(
                    face = face,
                    imageWidth = imageResource.width,
                    imageHeight = imageResource.height,
                    onClick = {
                        selectedFace = face
                        showTagDialog = true
                    }
                )
            }
        }

        // Close button
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    // Tag dialog
    if (showTagDialog && selectedFace != null) {
        TagDialog(
            onDismiss = {
                showTagDialog = false
                selectedFace = null
            },
            onConfirm = { tag ->
                selectedFace?.let { face ->
                    onTagFace(face, tag)
                }
                showTagDialog = false
                selectedFace = null
            }
        )
    }
}

@Composable
private fun FaceBox(
    face: Face,
    imageWidth: Int,
    imageHeight: Int,
    onClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val (scale, offsetX, offsetY) = calculateFitScaleAndOffset(
                imageWidth = imageWidth.toFloat(),
                imageHeight = imageHeight.toFloat(),
                containerWidth = size.width,
                containerHeight = size.height
            )

            val boundingBox = face.boundingBox
            val left = (boundingBox.left * scale) + offsetX
            val top = (boundingBox.top * scale) + offsetY
            val right = (boundingBox.right * scale) + offsetX
            val bottom = (boundingBox.bottom * scale) + offsetY

            drawRect(
                color = primaryColor,
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top),
                style = Stroke(width = 4.dp.toPx())
            )
        }
    }
}

@Composable
private fun TagDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var tagText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tag Face") },
        text = {
            TextField(
                value = tagText,
                onValueChange = { tagText = it },
                label = { Text("Enter tag") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(tagText) },
                enabled = tagText.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun calculateFitScaleAndOffset(
    imageWidth: Float,
    imageHeight: Float,
    containerWidth: Float,
    containerHeight: Float
): Triple<Float, Float, Float> {
    // Calculate the minimum scaling factor to fit the container
    val scaleX = containerWidth / imageWidth
    val scaleY = containerHeight / imageHeight
    val scale = minOf(scaleX, scaleY)

    // Calculate centered position
    val scaledWidth = imageWidth * scale
    val scaledHeight = imageHeight * scale
    val offsetX = (containerWidth - scaledWidth) / 2f
    val offsetY = (containerHeight - scaledHeight) / 2f

    return Triple(scale, offsetX, offsetY)
}