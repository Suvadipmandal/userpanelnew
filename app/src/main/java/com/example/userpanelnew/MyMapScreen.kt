package com.example.userpanelnew

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyMapScreen(
    modifier: Modifier = Modifier,
    viewModel: MyMapViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    
    // Search state
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (locationGranted) {
            viewModel.startLocationUpdates()
        }
    }
    
    LaunchedEffect(Unit) {
        if (!hasLocationPermission(context)) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            viewModel.startLocationUpdates()
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Mapbox Map - Fullscreen
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
                        // Enable location component with enhanced settings
                        location.updateSettings {
                            enabled = true
                            pulsingEnabled = true
                            pulsingColor = Color.BLUE
                            pulsingMaxRadius = 20.0f
                        }
                        
                        // Set initial camera position (will be overridden by location)
                        getMapboxMap().setCamera(
                            CameraOptions.Builder()
                                .center(Point.fromLngLat(-74.006, 40.7128))
                                .zoom(16.0)
                                .build()
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { mapView ->
                uiState.currentLocation?.let { location ->
                    val point = Point.fromLngLat(location.longitude, location.latitude)
                    mapView.getMapboxMap().setCamera(
                        CameraOptions.Builder()
                            .center(point)
                            .zoom(16.0)
                            .build()
                    )
                }
            }
        )
        
        // Search Bar - Top of screen with card-like appearance
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = { /* Handle search */ },
            active = isSearchActive,
            onActiveChange = { isSearchActive = it },
            placeholder = { Text("Search for bus or stop…") },
            leadingIcon = { 
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { searchQuery = "" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search"
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface,
                dividerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
            ),
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp
        ) {
            // Search suggestions can go here
            LazyColumn {
                items(5) { index ->
                    ListItem(
                        headlineContent = { Text("Search result ${index + 1}") },
                        leadingContent = { 
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
        
        // My Location Button - Bottom-end
        FloatingActionButton(
            onClick = {
                scope.launch {
                    uiState.currentLocation?.let { location ->
                        delay(500)
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "My Location"
            )
        }
        
        // Location Permission Status - Top overlay
        if (!uiState.hasLocationPermission) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp, start = 16.dp, end = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Location permission required",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

private fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}

private suspend fun animateCameraToLocation(context: Context, location: Location) {
    // This function will be called when the My Location button is pressed
    // The actual camera animation is handled by the Mapbox viewport plugin
    // For now, we'll just delay to simulate the animation
    delay(500)
}
