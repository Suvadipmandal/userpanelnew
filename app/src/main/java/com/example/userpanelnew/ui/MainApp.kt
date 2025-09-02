package com.example.userpanelnew.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.userpanelnew.navigation.Screen
import com.example.userpanelnew.ui.auth.LoginScreen
import com.example.userpanelnew.ui.auth.RegisterScreen
import com.example.userpanelnew.ui.components.PermissionDialog
import com.example.userpanelnew.ui.screens.*
import com.example.userpanelnew.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val viewModel: MainViewModel = viewModel()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val locationPermissionGranted by viewModel.locationPermissionGranted.collectAsState()
    
    val context = LocalContext.current
    
    // Check actual permission status on startup
    LaunchedEffect(Unit) {
        // Check if permissions are already granted by the system
        val hasFineLocation = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        
        val hasCoarseLocation = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        
        if (hasFineLocation || hasCoarseLocation) {
            viewModel.setLocationPermission(true)
        }
    }
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.setLocationPermission(locationGranted)
    }
    
    // Show permission dialog only if permissions are not granted
    LaunchedEffect(locationPermissionGranted) {
        if (!locationPermissionGranted) {
            // Check again to make sure we have the latest permission status
            val hasFineLocation = androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            
            val hasCoarseLocation = androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            
            if (hasFineLocation || hasCoarseLocation) {
                viewModel.setLocationPermission(true)
            }
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
                    Screen.NearbyBuses -> NearbyBusesScreen(
                        viewModel = viewModel,
                        onNavigateToHome = { selectedScreen = Screen.Home }
                    )
                    Screen.ProfileSettings -> ProfileSettingsScreen(viewModel = viewModel)
                    else -> HomeScreen(viewModel = viewModel)
                }
                

            }
            
            // Enhanced Bottom Navigation Bar
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                tonalElevation = 12.dp,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .padding(top = 4.dp)
            ) {
                // Navigation items
                NavigationBarItem(
                    selected = selectedScreen == Screen.Home,
                    onClick = { selectedScreen = Screen.Home },
                    icon = { 
                        Icon(
                            Icons.Default.Home, 
                            contentDescription = null,
                            modifier = Modifier.size(26.dp)
                        ) 
                    },
                    label = { 
                        Text(
                            text = Screen.Home.title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedScreen == Screen.Home) FontWeight.SemiBold else FontWeight.Medium,
                                letterSpacing = 0.1.sp
                            ),
                            color = if (selectedScreen == Screen.Home) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 2.dp)
                        ) 
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                    )
                )
                
                NavigationBarItem(
                    selected = selectedScreen == Screen.Stops,
                    onClick = { selectedScreen = Screen.Stops },
                    icon = { 
                        Icon(
                            Icons.Default.LocationOn, 
                            contentDescription = null,
                            modifier = Modifier.size(26.dp)
                        ) 
                    },
                    label = { 
                        Text(
                            text = Screen.Stops.title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedScreen == Screen.Stops) FontWeight.SemiBold else FontWeight.Medium,
                                letterSpacing = 0.1.sp
                            ),
                            color = if (selectedScreen == Screen.Stops) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 2.dp)
                        ) 
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                    )
                )
                
                NavigationBarItem(
                    selected = selectedScreen == Screen.NearbyBuses,
                    onClick = { selectedScreen = Screen.NearbyBuses },
                    icon = { 
                        Icon(
                            Icons.Default.LocationOn, 
                            contentDescription = null,
                            modifier = Modifier.size(26.dp)
                        ) 
                    },
                    label = { 
                        Text(
                            text = Screen.NearbyBuses.title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedScreen == Screen.NearbyBuses) FontWeight.SemiBold else FontWeight.Medium,
                                letterSpacing = 0.1.sp
                            ),
                            color = if (selectedScreen == Screen.NearbyBuses) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 2.dp)
                        ) 
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                    )
                )
                
                NavigationBarItem(
                    selected = selectedScreen == Screen.ProfileSettings,
                    onClick = { selectedScreen = Screen.ProfileSettings },
                    icon = { 
                        Icon(
                            Icons.Default.Person, 
                            contentDescription = null,
                            modifier = Modifier.size(26.dp)
                        ) 
                    },
                    label = { 
                        Text(
                            text = Screen.ProfileSettings.title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedScreen == Screen.ProfileSettings) FontWeight.SemiBold else FontWeight.Medium,
                                letterSpacing = 0.1.sp
                            ),
                            color = if (selectedScreen == Screen.ProfileSettings) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 2.dp)
                        ) 
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                    )
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
