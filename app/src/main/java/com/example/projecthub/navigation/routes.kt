package com.example.projecthub.navigation

 sealed class routes(val route : String) {
    object loginPage : routes("login_page")
    object signupPage : routes("signup_page")
    object homePage : routes("home_page")
    object profileSetupPage : routes("profile_setup_page")
    object onBoardingPage : routes("onBoarding_page")
    object profilePage : routes("profile_page")
    object settingsScreen : routes("settings_page")
}