package com.example.entradalivres.presentation.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.entradalivres.di.AppContainer
import com.example.entradalivres.presentation.ui.feature_login.LoginScreen

const val loginRoute = "loginRoute"

fun NavGraphBuilder.loginScreen(container: AppContainer, navController: NavHostController) {
    composable(loginRoute) {
        LoginScreen(container, navController)
    }
}

fun NavController.navigateToLoginScreenWithPopUp() {
    navigate(loginRoute) {
        popUpTo(0) {
            inclusive = true
        }
    }
}
