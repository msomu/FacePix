package com.msomu.facepix.ui.components

import android.graphics.Paint
import android.graphics.Typeface
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import com.msomu.facepix.database.model.PersonEntity
import com.msomu.facepix.model.Face

data class TaggedFaceInfo(
    val face: Face,
    val personEntity: PersonEntity? = null
)

@Composable
fun FaceDetectionOverlay(
    results: List<TaggedFaceInfo>,
    imageWidth: Int,
    imageHeight: Int,
    modifier: Modifier = Modifier,
    onFaceClicked: (Face) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val textPaint = remember {
        Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 40f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
        }
    }
    val textBackgroundPaint = remember {
        Paint().apply {
            color = android.graphics.Color.BLACK
            alpha = 160 // Semi-transparent background
        }
    }

    // Remember the scaled boxes to avoid recalculation on each click
    val scaledBoxes = remember(results, imageWidth, imageHeight) {
        mutableListOf<Pair<Rect, Face>>()
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .pointerInput(results) {
                detectTapGestures { offset ->
                    val clickedFace = scaledBoxes.firstOrNull { (rect, _) ->
                        rect.contains(offset)
                    }?.second

                    clickedFace?.let { face ->
                        onFaceClicked(face)
                    }
                }
            }
    ) {
        val (scale, offsetX, offsetY) = calculateFitScaleAndOffset(
            imageWidth = imageWidth.toFloat(),
            imageHeight = imageHeight.toFloat(),
            containerWidth = size.width,
            containerHeight = size.height
        )

        scaledBoxes.clear()

        results.forEach { taggedFace ->
            val boundingBox = taggedFace.face.boundingBox

            // Scale coordinates
            val left = (boundingBox.left * scale) + offsetX
            val top = (boundingBox.top * scale) + offsetY
            val right = (boundingBox.right * scale) + offsetX
            val bottom = (boundingBox.bottom * scale) + offsetY

            scaledBoxes.add(
                Rect(
                    left = left,
                    top = top,
                    right = right,
                    bottom = bottom
                ) to taggedFace.face
            )

            // Determine box color based on whether face is tagged
            val boxColor = if (taggedFace.personEntity != null) secondaryColor else primaryColor

            // Draw bounding box
            drawRect(
                color = boxColor,
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top),
                style = Stroke(width = 8f)
            )


            taggedFace.personEntity?.let { personEntity ->
                drawNameTag(
                    name = personEntity.name,
                    x = left,
                    y = top - 10f,
                    textPaint = textPaint,
                    backgroundPaint = textBackgroundPaint
                )
            }
        }
    }
}

private fun DrawScope.drawNameTag(
    name: String,
    x: Float,
    y: Float,
    textPaint: Paint,
    backgroundPaint: Paint
) {
    drawIntoCanvas { canvas ->
        val textWidth = textPaint.measureText(name)
        val textHeight = -textPaint.ascent() + textPaint.descent()
        val padding = 8f

        // Calculate text position
        val textY = y - padding  // Move text above the box
        val backgroundTop = textY - textHeight - padding
        val backgroundBottom = textY + padding

        // Draw text background
        canvas.nativeCanvas.drawRect(
            x - padding,
            backgroundTop,
            x + textWidth + padding,
            backgroundBottom,
            backgroundPaint
        )

        // Draw text
        canvas.nativeCanvas.drawText(
            name,
            x,
            textY,
            textPaint
        )
    }
}

private fun calculateFitScaleAndOffset(
    imageWidth: Float,
    imageHeight: Float,
    containerWidth: Float,
    containerHeight: Float
): Triple<Float, Float, Float> {
    val scaleX = containerWidth / imageWidth
    val scaleY = containerHeight / imageHeight
    val scale = minOf(scaleX, scaleY)

    val scaledWidth = imageWidth * scale
    val scaledHeight = imageHeight * scale
    val offsetX = (containerWidth - scaledWidth) / 2f
    val offsetY = (containerHeight - scaledHeight) / 2f

    return Triple(scale, offsetX, offsetY)
}