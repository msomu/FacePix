package com.msomu.facepix.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.msomu.facepix.database.model.PersonEntity
import com.msomu.facepix.model.Face

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageDetailScreen(
    uiState: ImageDetailUiState,
    availablePersons: List<PersonEntity>,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onPersonSelected: (Face, Long) -> Unit,
    onPersonCreated: (String) -> Unit
) {
    var selectedFace by remember { mutableStateOf<Face?>(null) }
    var showPersonDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Image Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is ImageDetailUiState.Loading -> {
                Box(modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }

            is ImageDetailUiState.Success -> {
                Column(
                    modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    var showOverlay by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = state.image.imagePath,
                                onState = { state ->
                                    if (state is AsyncImagePainter.State.Success) {
                                        showOverlay = true
                                    }
                                }
                            ),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                        this@Column.AnimatedVisibility(showOverlay) {
                            FaceDetectionOverlay(
                                results = state.faces,
                                imageWidth = state.image.width,
                                imageHeight = state.image.height,
                                modifier = Modifier.fillMaxSize(),
                                onFaceClicked = { face ->
                                    selectedFace = face
                                    showPersonDialog = true
                                }
                            )
                        }
                    }
                }
            }

            is ImageDetailUiState.Error -> {
                Text(state.message)
            }
        }
    }

    // Person Selection Dialog
    if (showPersonDialog && selectedFace != null) {
        PersonSelectionDialog(
            availablePersons = availablePersons,
            onPersonSelected = { person ->
                selectedFace?.let { face ->
                    onPersonSelected(face, person.id)
                }
                showPersonDialog = false
                selectedFace = null
            },
            onCreatePerson = { name ->
                onPersonCreated(name)
            },
            onDismiss = {
                showPersonDialog = false
                selectedFace = null
            }
        )
    }
}

@Composable
private fun PersonSelectionDialog(
    availablePersons: List<PersonEntity>,
    onPersonSelected: (PersonEntity) -> Unit,
    onCreatePerson: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var isAddingNewPerson by remember { mutableStateOf(false) }
    var newPersonName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isAddingNewPerson) "Add New Person" else "Select Person") },
        text = {
            if (isAddingNewPerson) {
                OutlinedTextField(
                    value = newPersonName,
                    onValueChange = { newPersonName = it },
                    label = { Text("Person Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Column {
                    availablePersons.forEach { person ->
                        TextButton(
                            onClick = { onPersonSelected(person) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(person.name)
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (isAddingNewPerson) {
                TextButton(
                    onClick = {
                        if (newPersonName.isNotBlank()) {
                            onCreatePerson(newPersonName)
                            isAddingNewPerson = false
                            newPersonName = ""
                        }
                    }
                ) {
                    Text("Add")
                }
            } else {
                TextButton(
                    onClick = { isAddingNewPerson = true }
                ) {
                    Text("Add New Person")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                if (isAddingNewPerson) {
                    isAddingNewPerson = false
                    newPersonName = ""
                } else {
                    onDismiss()
                }
            }) {
                Text(if (isAddingNewPerson) "Cancel" else "Close")
            }
        }
    )
}