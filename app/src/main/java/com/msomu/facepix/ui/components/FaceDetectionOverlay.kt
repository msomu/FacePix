package com.msomu.facepix.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import com.msomu.facepix.model.Face
import kotlin.math.max

@Composable
fun FaceDetectionOverlay(
    results: List<Face>,
    imageWidth: Int,
    imageHeight: Int,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier.fillMaxSize()) {
        val (scale, offsetX, offsetY) = calculateCenterCropScaleAndOffset(
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

private fun calculateCenterCropScaleAndOffset(
    imageWidth: Float,
    imageHeight: Float,
    containerWidth: Float,
    containerHeight: Float
): Triple<Float, Float, Float> {
    // Calculate scale to fill container while maintaining aspect ratio
    val scaleX = containerWidth.toFloat() / imageWidth.toFloat()
    val scaleY = containerHeight.toFloat() / imageHeight.toFloat()
    val scale = max(scaleX, scaleY)

    // Calculate offset to center the image
    val scaledImageWidth = imageWidth * scale
    val scaledImageHeight = imageHeight * scale
    val offsetX = (containerWidth - scaledImageWidth) / 2f
    val offsetY = (containerHeight - scaledImageHeight) / 2f

    return Triple(scale, offsetX, offsetY)
}