package com.msomu.facepix

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import com.msomu.facepix.model.DummyImageResource
import com.msomu.facepix.model.Face
import com.msomu.facepix.model.ImageResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CompositePhotosRepository @Inject constructor(
    private val applicationContext: Application
) : PhotosRepository {

    override fun getAllPhotosFromStorage(): Flow<List<ImageResource>> = flow {
        val processedImages = mutableListOf<ImageResource>()
        val projection = arrayOf(MediaStore.Images.Media.DATA)

        applicationContext.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null
        )?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            while (cursor.moveToNext()) {
                val imagePath = cursor.getString(columnIndex)
                val bitmap = BitmapFactory.decodeFile(imagePath)
                val faces = runDetectionOnImage(bitmap)
                if (faces.isNotEmpty()) {
                    val imageResource = ImageResource(
                        imagePath = imagePath,
                        height = bitmap.height,
                        width = bitmap.width,
                        faces = faces
                    )
                    processedImages.add(imageResource)
                    // Emit the updated list after each image is processed
                    emit(processedImages.toList())
                }
                // Make sure to recycle the bitmap to free up memory
                bitmap.recycle()
            }
        }
    }.flowOn(Dispatchers.IO)

    private fun runDetectionOnImage(bitmap: Bitmap): List<Face> {
        val faceDetectorHelper = FaceDetectorHelper(context = applicationContext)
        val face = mutableListOf<Face>()
        faceDetectorHelper.detectImage(bitmap)?.let { resultBundle ->
            resultBundle.results.forEach {
                for (detection in it.detections()) {
                    val boundingBox = detection.boundingBox()
                    face.add(
                        Face(
                            boundingBox = boundingBox,
                            confidence = detection.categories()[0].score()
                        )
                    )
                }
            }
        }
        return face
    }
}