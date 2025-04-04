package com.example.projecthub.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projecthub.screens.OnBoardingScreen
import com.example.projecthub.screens.ProfileSetupScreen
import com.example.projecthub.screens.homePage
import com.example.projecthub.screens.loginPage
import com.example.projecthub.screens.profileScreen
import com.example.projecthub.screens.signupPage
import com.example.projecthub.viewModel.authViewModel

@Composable
fun appNavigation(modifier: Modifier,authViewModel: authViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController , startDestination = "login_page", builder = {
        composable("login_page") {
            loginPage(Modifier,navController,authViewModel)
        }
        composable("signup_page") {
            signupPage(Modifier,navController,authViewModel)
        }
        composable(routes.homePage.route) {
            homePage(Modifier, navController, authViewModel)
        }
        composable(routes.profileSetupPage.route) {
            ProfileSetupScreen(navController)
        }
        composable(routes.onBoardingPage.route) {
            OnBoardingScreen(navController, authViewModel)
        }
        composable(routes.profilePage.route) {
            profileScreen(navController)
        }
    })
}