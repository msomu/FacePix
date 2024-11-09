package com.msomu.facepix

import com.msomu.facepix.model.ImageResource
import kotlinx.coroutines.flow.Flow

interface PhotosRepository {
    fun getAllPhotosFromStorage():Flow<List<ImageResource>>
}