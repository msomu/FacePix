package com.msomu.facepix

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msomu.facepix.ui.HomePageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val photosRepository: CompositePhotosRepository
) : ViewModel() {

    init {
        syncImages()
    }

    val imageState: StateFlow<HomePageUiState> =
        photosRepository.getAllPhotosFromStorage()
            .map { images ->
                if (images.isEmpty()) {
                    HomePageUiState.Loading
                } else {
                    HomePageUiState.Success(images = images)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomePageUiState.Loading,
            )

    private fun syncImages() {
        viewModelScope.launch {
            photosRepository.syncImagesWithStorage().collect { }
        }
    }
}