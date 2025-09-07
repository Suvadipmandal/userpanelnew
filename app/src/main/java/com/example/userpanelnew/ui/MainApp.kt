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
import android.widget.Toast
import com.example.userpanelnew.navigation.Screen
import com.example.userpanelnew.ui.auth.LoginScreen
import com.example.userpanelnew.ui.auth.RegisterScreen
import com.example.userpanelnew.ui.screens.*
import com.example.userpanelnew.utils.LocalizationHelper
import com.example.userpanelnew.utils.NavigationEventBus
import com.example.userpanelnew.utils.NavigationEvent
import com.example.userpanelnew.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val viewModel: MainViewModel = viewModel()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val locationPermissionGranted by viewModel.locationPermissionGranted.collectAsState()
    val notificationPermissionGranted by viewModel.notificationPermissionGranted.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    
    val context = LocalContext.current
    val localizedContext = remember(currentLanguage) {
        LocalizationHelper.setLocale(context, currentLanguage)
    }
    
    // Initialize language preference manager
    LaunchedEffect(Unit) {
        viewModel.initializeLanguagePreferenceManager(context)
    }
    
    // Language change handling
    LaunchedEffect(Unit) {
        viewModel.setLanguageChangeCallback { language ->
            // Language change is handled automatically through StateFlow
            // The UI will recompose when currentLanguage changes
            println("Language changed to: ${language.displayName}")
        }
    }
    
    // Debug: Log when language changes
    LaunchedEffect(currentLanguage) {
        println("Current language updated to: ${currentLanguage.displayName}")
    }
    
    // Check actual permission status on startup
    LaunchedEffect(Unit) {
        // Check if location permissions are already granted by the system
        val hasFineLocation = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        
        val hasCoarseLocation = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        
        println("DEBUG: Location permissions - Fine: $hasFineLocation, Coarse: $hasCoarseLocation")
        
        if (hasFineLocation || hasCoarseLocation) {
            viewModel.setLocationPermission(true)
        }
        
        // Check if notification permission is already granted by the system
        val hasNotificationPermission = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        
        println("DEBUG: Notification permission: $hasNotificationPermission")
        
        if (hasNotificationPermission) {
            viewModel.setNotificationPermission(true)
        }
    }
    
    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        println("DEBUG: Location permission result: $locationGranted")
        viewModel.setLocationPermission(locationGranted)
        if (locationGranted) {
            Toast.makeText(context, "Location access granted! You can now track buses.", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Location access denied. Some features may not work properly.", Toast.LENGTH_LONG).show()
        }
    }
    
    // Notification permission launcher
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        println("DEBUG: Notification permission result: $isGranted")
        viewModel.setNotificationPermission(isGranted)
        if (isGranted) {
            Toast.makeText(context, "Notifications enabled! You'll receive bus updates.", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Notifications disabled. You can enable them later in settings.", Toast.LENGTH_LONG).show()
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
        var showBusTracking by remember { mutableStateOf(false) }
        var trackingBusId by remember { mutableStateOf("") }
        
        // Listen for navigation events from notifications
        LaunchedEffect(Unit) {
            NavigationEventBus.events.collect { event ->
                when (event) {
                    is NavigationEvent.NavigateToTracking -> {
                        trackingBusId = event.busId
                        showBusTracking = true
                    }
                }
            }
        }
        
        if (showBusTracking) {
            // Full screen bus tracking - no navigation bar
            QuickBusTrackingScreen(
                busId = trackingBusId,
                onBackPressed = { 
                    showBusTracking = false
                    trackingBusId = ""
                }
            )
         } else {
             // Normal app with navigation bar
             Column(modifier = Modifier.fillMaxSize()) {
                 // Permission status indicator (for debugging)
                 if (!locationPermissionGranted || !notificationPermissionGranted) {
                     Card(
                         modifier = Modifier
                             .fillMaxWidth()
                             .padding(8.dp),
                         colors = CardDefaults.cardColors(
                             containerColor = MaterialTheme.colorScheme.errorContainer
                         )
                     ) {
                         Row(
                             modifier = Modifier.padding(16.dp),
                             verticalAlignment = Alignment.CenterVertically
                         ) {
                             Icon(
                                 Icons.Default.Warning,
                                 contentDescription = null,
                                 tint = MaterialTheme.colorScheme.onErrorContainer
                             )
                             Spacer(modifier = Modifier.width(8.dp))
                             Text(
                                 text = "Permissions needed: Location: $locationPermissionGranted, Notifications: $notificationPermissionGranted",
                                 style = MaterialTheme.typography.bodySmall,
                                 color = MaterialTheme.colorScheme.onErrorContainer
                             )
                         }
                     }
                 }
                 
                 // Content area
                 Box(
                     modifier = Modifier
                         .fillMaxSize()
                         .weight(1f)
                 ) {
                     when (selectedScreen) {
                         Screen.Home -> HomeScreen(
                             viewModel = viewModel,
                             onRequestLocationPermission = {
                                 locationPermissionLauncher.launch(
                                     arrayOf(
                                         android.Manifest.permission.ACCESS_FINE_LOCATION,
                                         android.Manifest.permission.ACCESS_COARSE_LOCATION
                                     )
                                 )
                             },
                             onRequestNotificationPermission = {
                                 notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                             }
                         )
                         Screen.Stops -> StopsScreen(viewModel = viewModel)
                         Screen.NearbyBuses -> NearbyBusesScreen(
                             viewModel = viewModel,
                             onNavigateToHome = { selectedScreen = Screen.Home },
                             onNavigateToTracking = { busId ->
                                 trackingBusId = busId
                                 showBusTracking = true
                             }
                         )
                         Screen.ProfileSettings -> ProfileSettingsScreen(viewModel = viewModel)
                         else -> HomeScreen(viewModel = viewModel)
                     }
                 }
                
                // Enhanced Bottom Navigation Bar with Google Maps-like styling
                NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 16.dp,
                modifier = Modifier
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
                    .padding(top = 12.dp, bottom = 12.dp)
            ) {
                // Navigation items
                NavigationBarItem(
                    selected = selectedScreen == Screen.Home,
                    onClick = { selectedScreen = Screen.Home },
                    alwaysShowLabel = true,
                    icon = { 
                        Icon(
                            Icons.Default.Home, 
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        ) 
                    },
                    label = { 
                        Text(
                            text = "Home",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedScreen == Screen.Home) FontWeight.SemiBold else FontWeight.Medium,
                                letterSpacing = 0.1.sp
                            ),
                            color = if (selectedScreen == Screen.Home) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        ) 
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                )
                
                NavigationBarItem(
                    selected = selectedScreen == Screen.Stops,
                    onClick = { selectedScreen = Screen.Stops },
                    alwaysShowLabel = true,
                    icon = { 
                        Icon(
                            Icons.Default.Place, 
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        ) 
                    },
                    label = { 
                        Text(
                            text = "Stops",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedScreen == Screen.Stops) FontWeight.SemiBold else FontWeight.Medium,
                                letterSpacing = 0.1.sp
                            ),
                            color = if (selectedScreen == Screen.Stops) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        ) 
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                )
                
                NavigationBarItem(
                    selected = selectedScreen == Screen.NearbyBuses,
                    onClick = { selectedScreen = Screen.NearbyBuses },
                    alwaysShowLabel = true,
                    icon = { 
                        Icon(
                            Icons.Default.LocationOn, 
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        ) 
                    },
                    label = { 
                        Text(
                            text = "Nearby Buses",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedScreen == Screen.NearbyBuses) FontWeight.SemiBold else FontWeight.Medium,
                                letterSpacing = 0.1.sp
                            ),
                            color = if (selectedScreen == Screen.NearbyBuses) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        ) 
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                )
                
                NavigationBarItem(
                    selected = selectedScreen == Screen.ProfileSettings,
                    onClick = { selectedScreen = Screen.ProfileSettings },
                    alwaysShowLabel = true,
                    icon = { 
                        Icon(
                            Icons.Default.Person, 
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        ) 
                    },
                    label = { 
                        Text(
                            text = "Profile",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedScreen == Screen.ProfileSettings) FontWeight.SemiBold else FontWeight.Medium,
                                letterSpacing = 0.1.sp
                            ),
                            color = if (selectedScreen == Screen.ProfileSettings) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        ) 
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                )
            }
            }
        }
    
     // Simple permission request logic
     LaunchedEffect(Unit) {
         // Small delay to ensure UI is ready
         kotlinx.coroutines.delay(1000)
         
         println("DEBUG: Starting permission check - Location: $locationPermissionGranted, Notification: $notificationPermissionGranted")
         
         // Request location permission first if not granted
         if (!locationPermissionGranted) {
             println("DEBUG: Requesting location permission")
             locationPermissionLauncher.launch(
                 arrayOf(
                     android.Manifest.permission.ACCESS_FINE_LOCATION,
                     android.Manifest.permission.ACCESS_COARSE_LOCATION
                 )
             )
         }
     }
     
     // Manual permission request fallback
     LaunchedEffect(Unit) {
         // If permissions are still not granted after 3 seconds, try again
         kotlinx.coroutines.delay(3000)
         
         if (!locationPermissionGranted) {
             println("DEBUG: Fallback - Requesting location permission again")
             locationPermissionLauncher.launch(
                 arrayOf(
                     android.Manifest.permission.ACCESS_FINE_LOCATION,
                     android.Manifest.permission.ACCESS_COARSE_LOCATION
                 )
             )
         }
     }
     
     // Request notification permission after location is granted
     LaunchedEffect(locationPermissionGranted) {
         if (locationPermissionGranted && !notificationPermissionGranted) {
             // Small delay to ensure location permission is fully processed
             kotlinx.coroutines.delay(500)
             println("DEBUG: Requesting notification permission")
             notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
         }
     }
    }
}
