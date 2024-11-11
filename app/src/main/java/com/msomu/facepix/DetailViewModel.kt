package com.msomu.facepix

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.msomu.facepix.database.dao.PersonDao
import com.msomu.facepix.database.dao.ProcessedImageDao
import com.msomu.facepix.database.model.PersonEntity
import com.msomu.facepix.model.Face
import com.msomu.facepix.ui.components.DetailsRoute
import com.msomu.facepix.ui.components.ImageDetailUiState
import com.msomu.facepix.ui.components.TaggedFaceInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val processedImageDao: ProcessedImageDao,
    private val personDao: PersonDao,
    private val imageRepository: ImageRepository
) : ViewModel() {
    private val route = savedStateHandle.toRoute<DetailsRoute>()

    private val imagePath: String = checkNotNull(route.imagePath)

    private val _uiState = MutableStateFlow<ImageDetailUiState>(ImageDetailUiState.Loading)
    val uiState: StateFlow<ImageDetailUiState> = _uiState.asStateFlow()

    private var currentFaces : List<TaggedFaceInfo> = emptyList()

    val availablePersons = personDao.getAllPersons()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            processedImageDao.getImage(imagePath).collect { image ->
            _uiState.value = if (image == null) {
                ImageDetailUiState.Error("Image not found")
            } else {
                currentFaces = image.detectedFaces.map { TaggedFaceInfo(it, personEntity = personDao.getPerson(it.personId ?: 0)) }
                ImageDetailUiState.Success(
                    image = image,
                    faces = currentFaces
                )
            }
        }
    }}

    fun onPersonSelected(face: Face, personId: Long) {
        viewModelScope.launch {
            imagePath.let { path ->
                // Find the index of the face in the current image
                imageRepository.updateFacePersonId(path, getFaceIndex(face), personId)
            }
        }
    }

    private fun getFaceIndex(face: Face): Int {
        // This method would need to be implemented based on how you want to identify
        // the specific face in the list. You could compare bounding boxes, confidence, etc.
        // For example:
        return currentFaces.indexOfFirst {
            it.face.boundingBox == face.boundingBox &&
                    it.face.confidence == face.confidence
        }
    }

    fun addPerson(name: String) {
        viewModelScope.launch {
            personDao.insertPerson(PersonEntity(name = name))
        }
    }
}