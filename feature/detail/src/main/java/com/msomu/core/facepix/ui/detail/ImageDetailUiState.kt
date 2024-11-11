package com.msomu.core.facepix.ui.detail

import com.msomu.facepix.core.database.model.ProcessedImageEntity
import com.msomu.facepix.core.database.model.TaggedFaceInfo


sealed interface ImageDetailUiState {
    data object Loading : ImageDetailUiState
    data class Success(
        val image: ProcessedImageEntity,
        val faces: List<TaggedFaceInfo>
    ) : ImageDetailUiState
    data class Error(val message: String) : ImageDetailUiState
}