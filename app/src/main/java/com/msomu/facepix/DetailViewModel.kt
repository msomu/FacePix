package com.msomu.facepix

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.msomu.facepix.database.dao.PersonDao
import com.msomu.facepix.database.dao.ProcessedImageDao
import com.msomu.facepix.database.model.PersonEntity
import com.msomu.facepix.ui.components.DetailsRoute
import com.msomu.facepix.ui.components.ImageDetailUiState
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
            val image = processedImageDao.getImage(imagePath)
            _uiState.value = if (image == null) {
                ImageDetailUiState.Error("Image not found")
            } else {
                ImageDetailUiState.Success(
                    image = image,
                )
            }
        }
    }

    fun addPerson(name: String) {
        viewModelScope.launch {
            personDao.insertPerson(PersonEntity(name = name))
        }
    }
}