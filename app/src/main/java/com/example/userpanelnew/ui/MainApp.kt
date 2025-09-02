package com.example.userpanelnew.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.userpanelnew.navigation.Screen
import com.example.userpanelnew.ui.auth.LoginScreen
import com.example.userpanelnew.ui.auth.RegisterScreen
import com.example.userpanelnew.ui.components.PermissionDialog
import com.example.userpanelnew.ui.screens.*
import com.example.userpanelnew.viewmodels.MainViewModel
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val viewModel: MainViewModel = viewModel()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val locationPermissionGranted by viewModel.locationPermissionGranted.collectAsState()
    
    val context = LocalContext.current
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.setLocationPermission(locationGranted)
    }
    
    // Show permission dialog when app starts
    LaunchedEffect(Unit) {
        if (!locationPermissionGranted) {
            // Show permission dialog after a short delay
            kotlinx.coroutines.delay(500)
        }
    }
    
    if (!isLoggedIn) {
        // Authentication flow
        var showLogin by remember { mutableStateOf(true) }
        
        if (showLogin) {
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = { showLogin = false },
                onLoginSuccess = { /* Will be handled by state change */ }
            )
        } else {
            RegisterScreen(
                viewModel = viewModel,
                onNavigateToLogin = { showLogin = true },
                onRegisterSuccess = { /* Will be handled by state change */ }
            )
        }
    } else {
        // Main app with Bottom Navigation Bar
        var selectedScreen by remember { mutableStateOf<Screen>(Screen.Home) }
        
        Column(modifier = Modifier.fillMaxSize()) {
            // Content area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (selectedScreen) {
                    Screen.Home -> HomeScreen(viewModel = viewModel)
                    Screen.Stops -> StopsScreen(viewModel = viewModel)
                    Screen.Profile -> ProfileScreen(viewModel = viewModel)
                    Screen.Settings -> SettingsScreen(viewModel = viewModel)
                    else -> HomeScreen(viewModel = viewModel)
                }
                
                // Floating Action Button for quick logout
                FloatingActionButton(
                    onClick = { viewModel.logout() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ) {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = "Logout"
                    )
                }
            }
            
            // Bottom Navigation Bar
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                // Navigation items
                NavigationBarItem(
                    selected = selectedScreen == Screen.Home,
                    onClick = { selectedScreen = Screen.Home },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text(Screen.Home.title) }
                )
                
                NavigationBarItem(
                    selected = selectedScreen == Screen.Stops,
                    onClick = { selectedScreen = Screen.Stops },
                    icon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    label = { Text(Screen.Stops.title) }
                )
                
                NavigationBarItem(
                    selected = selectedScreen == Screen.Profile,
                    onClick = { selectedScreen = Screen.Profile },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text(Screen.Profile.title) }
                )
                
                NavigationBarItem(
                    selected = selectedScreen == Screen.Settings,
                    onClick = { selectedScreen = Screen.Settings },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text(Screen.Settings.title) }
                )
            }
        }
    }
    
    // Permission Dialog
    if (!locationPermissionGranted) {
        PermissionDialog(
            onAllow = {
                permissionLauncher.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            onDeny = {
                viewModel.setLocationPermission(false)
            }
        )
    }
}
