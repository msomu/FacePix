package com.msomu.facepix.ui.components

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class DetailPage(val imagePath: String? = null)

fun NavController.navigateToDetail(imagePath: String? = null, navOptions: NavOptions? = null) {
    navigate(route = DetailPage(imagePath), navOptions)
}

fun NavGraphBuilder.detailScreen() {
    composable<DetailPage> {
        DetailPage()
    }
}