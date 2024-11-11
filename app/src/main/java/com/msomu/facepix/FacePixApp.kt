package com.msomu.facepix

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.msomu.facepix.navigation.HomePage
import com.msomu.facepix.navigation.detailScreen
import com.msomu.facepix.navigation.homeScreen
import com.msomu.facepix.navigation.navigateToDetail

@Composable
fun FacePixApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(modifier = modifier, navController = navController, startDestination = HomePage) {
        homeScreen(navController::navigateToDetail)
        detailScreen()
    }
}