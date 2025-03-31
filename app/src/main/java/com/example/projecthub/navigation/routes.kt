package com.example.projecthub.navigation

 sealed class routes(val route : String) {
    object loginPage : routes("login_page")
    object signupPage : routes("signup_page")
    object homePage : routes("home_page")
}