package com.msomu.facepix.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.msomu.facepix.model.Face

@Composable
fun FaceDetectionOverlay(
    results: List<Face>,
    imageWidth: Int,
    imageHeight: Int,
    modifier: Modifier = Modifier,
    onFaceClicked : (Face) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    val scaledBoxes = remember(results, imageWidth, imageHeight) {
        mutableListOf<Pair<Rect, Face>>()
    }

    Canvas(modifier = modifier
        .fillMaxSize()
        .clipToBounds()
        .pointerInput(results) {
            detectTapGestures { offset ->
                // Find the face whose bounding box contains the tap position
                val clickedFace = scaledBoxes.firstOrNull { (rect, _) ->
                    rect.contains(offset)
                }?.second

                clickedFace?.let { face ->
                    onFaceClicked(face)
                }
            }
        }) {
        val (scale, offsetX, offsetY) = calculateFitScaleAndOffset(
            imageWidth = imageWidth.toFloat(),
            imageHeight = imageHeight.toFloat(),
            containerWidth = size.width,
            containerHeight = size.height
        )

        scaledBoxes.clear()

        results.forEach { detection ->
            val boundingBox = detection.boundingBox

            // Scale coordinates
            val left = (boundingBox.left * scale) + offsetX
            val top = (boundingBox.top * scale) + offsetY
            val right = (boundingBox.right * scale) + offsetX
            val bottom = (boundingBox.bottom * scale) + offsetY

            scaledBoxes.add(
                Rect(
                    left,
                    top,
                    right,
                    bottom
                ) to detection
            )

            // Draw bounding box
            drawRect(
                color = primaryColor,
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top),
                style = Stroke(width = 8f)
            )
        }
    }
}

private fun calculateFitScaleAndOffset(
    imageWidth: Float,
    imageHeight: Float,
    containerWidth: Float,
    containerHeight: Float
): Triple<Float, Float, Float> {
    // Calculate the scaling factor to fit within the container
    val scaleX = containerWidth / imageWidth
    val scaleY = containerHeight / imageHeight
    // Use the smaller scaling factor to ensure the image fits entirely
    val scale = minOf(scaleX, scaleY)

    // Calculate centered position
    val scaledWidth = imageWidth * scale
    val scaledHeight = imageHeight * scale
    val offsetX = (containerWidth - scaledWidth) / 2f
    val offsetY = (containerHeight - scaledHeight) / 2f

    return Triple(scale, offsetX, offsetY)
}