package com.msomu.facepix.feature.home

import com.msomu.facepix.core.model.ImageResource

sealed interface HomePageUiState {
    /**
     * The pictures are still loading.
     */
    data object Loading : HomePageUiState
    data class Error(
        val message : String
    ) : HomePageUiState

    /**
     * The images is loaded with the given list of image resources.
     */
    data class Success(
        /**
         * The list of image resources contained in the home page.
         */
        val images: List<ImageResource>,
    ) : HomePageUiState
}