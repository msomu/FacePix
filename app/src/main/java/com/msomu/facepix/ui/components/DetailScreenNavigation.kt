package com.msomu.facepix.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.msomu.facepix.DetailViewModel
import kotlinx.serialization.Serializable
import timber.log.Timber

@Serializable
data class DetailsRoute(val imagePath: String? = null)

fun NavController.navigateToDetail(imagePath: String? = null, navOptions: NavOptions? = null) {
    navigate(route = DetailsRoute(imagePath), navOptions)
}

fun NavGraphBuilder.detailScreen() {
    composable<DetailsRoute> {
        DetailPage()
    }
}

@Composable
internal fun DetailPage(
    viewModel: DetailViewModel = hiltViewModel(),
){
    val imageResource = viewModel.selectedImagePath.collectAsStateWithLifecycle()
    Timber.d("DetailPage: $imageResource")
    DetailPage(imagePath = null, modifier = Modifier)
}