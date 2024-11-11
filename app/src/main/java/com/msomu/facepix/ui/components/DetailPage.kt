package com.msomu.facepix.ui.components

import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.msomu.facepix.database.model.PersonEntity
import com.msomu.facepix.model.Face

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageDetailScreen(
    uiState: ImageDetailUiState,
    availablePersons : List<PersonEntity>,
    modifier: Modifier = Modifier,
    tagFace: (RectF, Long, Float) -> Unit,
    addPerson: (String) -> Unit,
    onNavigateBack: () -> Unit
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
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        AsyncImage(
                            model = state.image.image.imagePath,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )

                        // Draw face boxes and tags
                        Canvas(Modifier.fillMaxSize()) {
                            state.faceTags.forEach { faceTag ->
                                drawFaceBox(
                                    boundingBox = faceTag.faceTag.face.boundingBox,
                                    person = faceTag.person,
                                    selected = selectedFace?.boundingBox == faceTag.faceTag.face.boundingBox
                                )
                            }
                        }

                        // Handle face selection
                        Box(
                            Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures { offset ->
                                        // Find tapped face
                                        selectedFace = state.image.image.faces.firstOrNull { face ->
                                            face.boundingBox.contains(offset.x, offset.y)
                                        }
                                        if (selectedFace != null) {
                                            showPersonDialog = true
                                        }
                                    }
                                }
                        )
                    }
                }
            }
            is ImageDetailUiState.Error -> {
                Text(state.message)
            }
        }
    }

    if (showPersonDialog && selectedFace != null) {
        PersonSelectionDialog(
            availablePersons = availablePersons,
            onPersonSelected = { person ->
                selectedFace?.let { face ->
                    tagFace(
                        face.boundingBox,
                        person.id,
                        face.confidence
                    )
                }
                showPersonDialog = false
                selectedFace = null
            },
            onAddNewPerson = { name ->
                addPerson(name)
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
    onAddNewPerson: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var showAddPerson by remember { mutableStateOf(false) }
    var newPersonName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Person") },
        text = {
            Column {
                if (showAddPerson) {
                    OutlinedTextField(
                        value = newPersonName,
                        onValueChange = { newPersonName = it },
                        label = { Text("Name") }
                    )
                } else {
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
            if (showAddPerson) {
                TextButton(
                    onClick = {
                        if (newPersonName.isNotBlank()) {
                            onAddNewPerson(newPersonName)
                            showAddPerson = false
                            newPersonName = ""
                        }
                    }
                ) {
                    Text("Add")
                }
            } else {
                TextButton(
                    onClick = { showAddPerson = true }
                ) {
                    Text("Add New Person")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun DrawScope.drawFaceBox(
    boundingBox: RectF,
    person: PersonEntity?,
    selected: Boolean
) {
    val color = if (selected) Color.Green else Color.Yellow
    drawRect(
        color = color,
        topLeft = Offset(boundingBox.left, boundingBox.top),
        size = androidx.compose.ui.geometry.Size(
            boundingBox.width(),
            boundingBox.height()
        ),
        style = androidx.compose.ui.graphics.drawscope.Stroke(
            width = 2f
        )
    )

    person?.let {
        drawIntoCanvas { canvas ->
            val paint = android.graphics.Paint().apply {
                setColor(android.graphics.Color.WHITE)
                textSize = 30f
                setShadowLayer(2f, 0f, 0f, android.graphics.Color.BLACK)
            }
            canvas.nativeCanvas.drawText(
                it.name,
                boundingBox.left,
                boundingBox.top - 5f,
                paint
            )
        }
    }
}