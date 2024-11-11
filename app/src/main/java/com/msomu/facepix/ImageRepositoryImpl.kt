package com.msomu.facepix

import com.msomu.facepix.database.dao.ProcessedImageDao
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(private val processedImageDao: ProcessedImageDao) :
    ImageRepository {
    override suspend fun updateFacePersonId(imagePath: String, faceIndex: Int, personId: Long) {
        val image = processedImageDao.getImageFromPath(imagePath) ?: return

        // Create a new list with the updated face
        val updatedFaces = image.detectedFaces.toMutableList()
        if (faceIndex < updatedFaces.size) {
            val oldFace = updatedFaces[faceIndex]
            updatedFaces[faceIndex] = oldFace.copy(personId = personId)

            // Create updated entity
            val updatedImage = image.copy(detectedFaces = updatedFaces)
            processedImageDao.updateProcessedImage(updatedImage)
        }
    }
}