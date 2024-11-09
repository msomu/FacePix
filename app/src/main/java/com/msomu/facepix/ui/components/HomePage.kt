package com.msomu.facepix.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.msomu.facepix.HomeViewModel
import com.msomu.facepix.ui.HomePageUiState
import timber.log.Timber

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomePage(
    modifier: Modifier,
    cameraPermissionStates: MultiplePermissionsState,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val imageState by viewModel.imageState.collectAsState()
    LaunchedEffect(imageState) { Timber.tag("msomu").d("HomePage: $imageState") }

    Column(modifier = modifier) {
        if (!cameraPermissionStates.allPermissionsGranted) {
            PermissionRequest(cameraPermissionStates)
        } else {
            when (imageState) {
                is HomePageUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is HomePageUiState.Success -> {
                    val images = (imageState as HomePageUiState.Success).images
                    ImageGrid(
                        Modifier.fillMaxSize(),
                        images = images
                    )
                }

                is HomePageUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text((imageState as HomePageUiState.Error).message)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionRequest(permissionStates: MultiplePermissionsState) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val textToShow = if (permissionStates.shouldShowRationale) {
            "The Gallery permission is important for this app. Please grant the permission."
        } else {
            "Gallery permission required for this feature to be available. " +
                    "Please grant the permissions"
        }
        Text(textToShow)
        Button(onClick = { permissionStates.launchMultiplePermissionRequest() }) {
            Text("Request permissions")
        }
    }
}