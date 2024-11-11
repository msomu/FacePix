package com.msomu.facepix.core.data

import com.msomu.facepix.core.model.ImageResource
import kotlinx.coroutines.flow.Flow

interface PhotosRepository {
    fun getAllPhotosFromStorage():Flow<List<ImageResource>>
}

interface ImageRepository {
    suspend fun updateFacePersonId(imagePath: String, faceIndex: Int, personId: Long)
}