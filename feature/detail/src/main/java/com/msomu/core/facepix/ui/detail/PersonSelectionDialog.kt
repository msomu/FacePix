package com.msomu.core.facepix.ui.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.msomu.facepix.core.database.model.PersonEntity

@Composable
fun PersonSelectionDialog(
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
                LazyColumn {
                    items(availablePersons) { person ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPersonSelected(person) }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = person.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
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