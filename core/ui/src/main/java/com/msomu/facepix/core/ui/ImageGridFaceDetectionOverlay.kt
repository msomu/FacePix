package com.msomu.facepix.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import com.msomu.facepix.core.model.Face

@Composable
fun ImageGridFaceDetectionOverlay(
    results: List<Face>,
    imageWidth: Int,
    imageHeight: Int,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier
        .fillMaxSize()
        .clipToBounds()) {
        val (scale, offsetX, offsetY) = calculateCropScaleAndOffset(
            imageWidth = imageWidth.toFloat(),
            imageHeight = imageHeight.toFloat(),
            containerWidth = size.width,
            containerHeight = size.height
        )

        results.forEach { detection ->
            val boundingBox = detection.boundingBox

            // Scale coordinates
            val left = (boundingBox.left * scale) + offsetX
            val top = (boundingBox.top * scale) + offsetY
            val right = (boundingBox.right * scale) + offsetX
            val bottom = (boundingBox.bottom * scale) + offsetY

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

private fun calculateCropScaleAndOffset(
    imageWidth: Float,
    imageHeight: Float,
    containerWidth: Float,
    containerHeight: Float
): Triple<Float, Float, Float> {
    // Calculate the scaling factor to fill the container
    val containerAspect = containerWidth / containerHeight
    val imageAspect = imageWidth / imageHeight

    val scale = if (containerAspect > imageAspect) {
        containerWidth / imageWidth
    } else {
        containerHeight / imageHeight
    }

    // Calculate centered position
    val scaledWidth = imageWidth * scale
    val scaledHeight = imageHeight * scale
    val offsetX = (containerWidth - scaledWidth) / 2f
    val offsetY = (containerHeight - scaledHeight) / 2f

    return Triple(scale, offsetX, offsetY)
}