package com.msomu.facepix.ui.components

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.msomu.facepix.ui.HomePageUiState
import timber.log.Timber

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomePage(
    homePageUiState: HomePageUiState,
    onImageClicked: (imagePath: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        listOf(READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(READ_MEDIA_IMAGES)
    } else {
        listOf(READ_EXTERNAL_STORAGE)
    }
    val cameraPermissionStates = rememberMultiplePermissionsState(
        permissions
    )

    LaunchedEffect(homePageUiState) { Timber.tag("msomu").d("HomePage: $homePageUiState") }

    Column(modifier = modifier) {
        if (!cameraPermissionStates.allPermissionsGranted) {
            PermissionRequest(cameraPermissionStates)
        } else {
            when (homePageUiState) {
                is HomePageUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is HomePageUiState.Success -> {
                    val images = (homePageUiState as HomePageUiState.Success).images
                    ImageGrid(
                        Modifier.fillMaxSize(),
                        images = images,
                        onImageClick = { imageResource -> onImageClicked(imageResource.imagePath) }
                    )
                }

                is HomePageUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text((homePageUiState as HomePageUiState.Error).message)
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