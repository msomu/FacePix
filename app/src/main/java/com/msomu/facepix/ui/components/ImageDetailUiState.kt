package com.msomu.facepix.ui.components

import com.msomu.facepix.database.model.FaceTagWithPerson
import com.msomu.facepix.database.model.ProcessedImageWithTags

sealed interface ImageDetailUiState {
    data object Loading : ImageDetailUiState
    data class Success(
        val image: ProcessedImageWithTags,
        val faceTags: List<FaceTagWithPerson>
    ) : ImageDetailUiState
    data class Error(val message: String) : ImageDetailUiState
}