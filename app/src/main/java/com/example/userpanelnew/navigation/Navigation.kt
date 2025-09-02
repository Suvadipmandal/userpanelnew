package com.example.userpanelnew.navigation

sealed class Screen(val route: String, val title: String) {
    object Login : Screen("login", "Login")
    object Register : Screen("register", "Register")
    object Home : Screen("home", "Home")
    object Stops : Screen("stops", "Stops & ETA")
    object Profile : Screen("profile", "Profile")
    object Settings : Screen("settings", "Settings")
}

sealed class AuthScreen(val route: String) {
    object Login : AuthScreen("auth_login")
    object Register : AuthScreen("auth_register")
}
