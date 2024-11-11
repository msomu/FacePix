package com.msomu.facepix.ui.components

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.msomu.facepix.ui.HomePageUiState

@Composable
fun HomePage(
    homePageUiState: HomePageUiState,
    refresh: () -> Unit,
    onImageClicked: (imagePath: String) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(modifier = modifier) {
        when (homePageUiState) {
            is HomePageUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is HomePageUiState.Success -> {
                val images = homePageUiState.images
                ImageGrid(Modifier.fillMaxSize(),
                    images = images,
                    onImageClick = { imageResource -> onImageClicked(imageResource.imagePath) })
            }

            is HomePageUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Text(homePageUiState.message)
                }
            }
        }
    }
    GalleryPermissionEffect(refresh)
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun GalleryPermissionEffect(refresh : () -> Unit) {
    // Permission requests should only be made from an Activity Context, which is not present
    // in previews
    if (LocalInspectionMode.current) return
    val permissions = if (VERSION.SDK_INT >= VERSION_CODES.UPSIDE_DOWN_CAKE) {
        listOf(READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED)
    } else if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
        listOf(READ_MEDIA_IMAGES)
    } else {
        listOf(READ_EXTERNAL_STORAGE)
    }
    val galleryPermissionsState = rememberMultiplePermissionsState(
        permissions, onPermissionsResult = {
            if (it.values.any { it }) {
                refresh()
            }
        }
    )
    LaunchedEffect(galleryPermissionsState) {
        if (galleryPermissionsState.permissions.any { it.status != PermissionStatus.Granted }) {
            galleryPermissionsState.launchMultiplePermissionRequest()
        }
    }
}