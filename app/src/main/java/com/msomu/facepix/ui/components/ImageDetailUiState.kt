package com.msomu.facepix.ui.components

import com.msomu.facepix.database.model.ProcessedImageEntity

sealed interface ImageDetailUiState {
    data object Loading : ImageDetailUiState
    data class Success(
        val image: ProcessedImageEntity,
        val faces: List<TaggedFaceInfo>
    ) : ImageDetailUiState
    data class Error(val message: String) : ImageDetailUiState
}