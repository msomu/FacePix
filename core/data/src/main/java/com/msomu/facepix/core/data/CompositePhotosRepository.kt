package com.msomu.facepix.core.data

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import com.msomu.facepix.core.database.dao.ProcessedImageDao
import com.msomu.facepix.core.database.model.ProcessedImageEntity
import com.msomu.facepix.core.facedetector.FaceDetectorHelper
import com.msomu.facepix.core.model.BoundingBox
import com.msomu.facepix.core.model.Face
import com.msomu.facepix.core.model.ImageResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.invoke
import java.io.File
import javax.inject.Inject

class CompositePhotosRepository @Inject constructor(
    private val applicationContext: Application,
    private val processedImageDao: ProcessedImageDao
) : PhotosRepository {

    private val faceDetectorHelper = FaceDetectorHelper(context = applicationContext)

    override fun getAllPhotosFromStorage(): Flow<List<ImageResource>> =
        processedImageDao.getAllImages()
            .map { entities ->
                entities.map { entity ->
                    ImageResource(
                        imagePath = entity.imagePath,
                        height = entity.height,
                        width = entity.width,
                        faces = entity.detectedFaces
                    )
                }
            }

    suspend fun syncImagesWithStorage() = (Dispatchers.IO) {
        val currentImages = mutableMapOf<String, Long>()
        val projection = arrayOf(
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_MODIFIED
        )

        // Get current images from storage
        applicationContext.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val modifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)

            while (cursor.moveToNext()) {
                val path = cursor.getString(pathColumn)
                val lastModified = cursor.getLong(modifiedColumn)
                currentImages[path] = lastModified
            }
        }

        // Process each image
        currentImages.forEach { (path, lastModified) ->
            val existingImage = processedImageDao.getImageFromPath(path)

            // Process only if image is new or modified
            if (existingImage == null || existingImage.lastModified < lastModified) {
                val bitmap = BitmapFactory.decodeFile(path)
                bitmap?.let {
                    try {
                        val faces = runDetectionOnImage(it)
                        if (faces.isNotEmpty()) {
                            val entity = ProcessedImageEntity(
                                imagePath = path,
                                height = it.height,
                                width = it.width,
                                lastModified = lastModified,
                                detectedFaces = faces
                            )
                            processedImageDao.insertImage(entity)
                        }
                    } finally {
                        it.recycle()
                    }
                }
            }
        }

        // Remove deleted images from database
        val dbPaths = processedImageDao.getAllImages()
            .map { it.map { entity -> entity.imagePath } }
            .first()
        dbPaths.forEach { path ->
            if (!currentImages.containsKey(path) || !File(path).exists()) {
                processedImageDao.deleteImage(path)
            }
        }
    }

    private fun runDetectionOnImage(bitmap: Bitmap): List<Face> {
        val face = mutableListOf<Face>()
        if (faceDetectorHelper.isClosed()) {
            faceDetectorHelper.setupFaceDetector()
        }
        faceDetectorHelper.detectImage(bitmap)?.let { resultBundle ->
            resultBundle.results.forEach {
                for (detection in it.detections()) {
                    val boundingBox = detection.boundingBox()
                    face.add(
                        Face(
                            boundingBox = BoundingBox(boundingBox.left, boundingBox.top, boundingBox.right, boundingBox.bottom),
                            confidence = detection.categories()[0].score()
                        )
                    )
                }
            }
        }
        faceDetectorHelper.clearFaceDetector()
        return face
    }
}