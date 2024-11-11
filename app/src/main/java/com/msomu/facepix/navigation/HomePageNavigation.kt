package com.msomu.facepix.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.msomu.facepix.feature.home.HomePageUi
import com.msomu.facepix.feature.home.HomeViewModel
import kotlinx.serialization.Serializable

@Serializable
data object HomePage

fun NavGraphBuilder.homeScreen(onImageClicked: (imagePath: String) -> Unit) {
    composable<HomePage>() {
        HomePage(onImageClicked = onImageClicked)
    }
}

@Composable
internal fun HomePage(
    onImageClicked : (imagePath: String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
){
    val homePageUiState by viewModel.imageState.collectAsStateWithLifecycle()
    HomePageUi(
        homePageUiState = homePageUiState,
        refresh = viewModel::syncImages,
        onImageClicked = onImageClicked,
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal,
                ),
            ),
    )
}