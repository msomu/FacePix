package com.msomu.facepix

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.msomu.facepix.ui.components.DetailsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
const val IMAGE_ID_KEY = "selectedImagePath"
@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val photosRepository: CompositePhotosRepository
) : ViewModel() {
    val route = savedStateHandle.toRoute<DetailsRoute>()
    val selectedImagePath: StateFlow<String?> = savedStateHandle.getStateFlow(
        key = IMAGE_ID_KEY,
        initialValue = route.imagePath,
    )

}