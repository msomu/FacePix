package com.msomu.facepix.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.msomu.core.facepix.ui.detail.DetailScreen
import com.msomu.core.facepix.ui.detail.DetailViewModel
import com.msomu.core.facepix.ui.detail.DetailsRoute

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
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val availablePersons by viewModel.availablePersons.collectAsStateWithLifecycle()
    DetailScreen(
        uiState = uiState,
        availablePersons = availablePersons,
        modifier = Modifier,
        onNavigateBack = {},
        onPersonSelected = viewModel::onPersonSelected,
        onPersonCreated = viewModel::addPerson,
    )
}