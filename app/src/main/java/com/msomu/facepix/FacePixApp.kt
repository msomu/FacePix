package com.msomu.facepix

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.msomu.facepix.ui.components.HomePage
import com.msomu.facepix.ui.components.detailScreen
import com.msomu.facepix.ui.components.homeScreen
import com.msomu.facepix.ui.components.navigateToDetail

@Composable
fun FacePixApp(modifier: Modifier = Modifier) {
    val snackbarHostState = remember { SnackbarHostState() }
    FacePixApp(
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
internal fun FacePixApp(snackbarHostState: SnackbarHostState, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HomePage) {
        homeScreen(navController::navigateToDetail)
        detailScreen()
    }
}