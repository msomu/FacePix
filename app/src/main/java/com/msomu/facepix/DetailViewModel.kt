package com.msomu.facepix

import android.graphics.RectF
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.msomu.facepix.database.dao.FaceTagDao
import com.msomu.facepix.database.dao.PersonDao
import com.msomu.facepix.database.dao.ProcessedImageDao
import com.msomu.facepix.database.model.FaceTagEntity
import com.msomu.facepix.database.model.PersonEntity
import com.msomu.facepix.model.Face
import com.msomu.facepix.ui.components.DetailsRoute
import com.msomu.facepix.ui.components.ImageDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val processedImageDao: ProcessedImageDao,
    private val personDao: PersonDao,
    private val faceTagDao: FaceTagDao,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<DetailsRoute>()

    private val imagePath: String = checkNotNull(route.imagePath)

    private val _uiState = MutableStateFlow<ImageDetailUiState>(ImageDetailUiState.Loading)
    val uiState: StateFlow<ImageDetailUiState> = _uiState.asStateFlow()

    val availablePersons = personDao.getAllPersons()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            combine(
                processedImageDao.getImageWithTags(imagePath),
                faceTagDao.getFaceTagsForImage(imagePath)
            ) { image, faceTags ->
                if (image == null) {
                    ImageDetailUiState.Error("Image not found")
                } else {
                    ImageDetailUiState.Success(
                        image = image,
                        faceTags = faceTags
                    )
                }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun addPerson(name: String) {
        viewModelScope.launch {
            personDao.insertPerson(PersonEntity(name = name))
        }
    }

    fun tagFace(boundingBox: RectF, personId: Long, confidence: Float) {
        viewModelScope.launch {
            faceTagDao.insertFaceTag(
                FaceTagEntity(
                    imagePath = imagePath,
                    face = Face(boundingBox,confidence),
                    personId = personId,
                )
            )
        }
    }

    fun removeFaceTag(faceTag: FaceTagEntity) {
        viewModelScope.launch {
            faceTagDao.deleteFaceTag(faceTag)
        }
    }
}