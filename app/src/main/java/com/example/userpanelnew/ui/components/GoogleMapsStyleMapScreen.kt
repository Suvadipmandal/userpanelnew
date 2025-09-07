package com.example.userpanelnew.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import android.util.Log
import com.example.userpanelnew.models.Bus
import com.example.userpanelnew.utils.LocationHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import com.mapbox.maps.extension.style.expressions.dsl.generated.*
import com.mapbox.maps.extension.style.layers.generated.*
import com.mapbox.maps.extension.style.layers.properties.generated.*
import com.mapbox.maps.extension.style.sources.generated.*
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.viewport
import kotlinx.coroutines.delay

// Logging tag
private const val TAG = "GoogleMapsStyleMapScreen"

// Google Maps-like color scheme
object GoogleMapsColors {
    val LAND = Color(0xFFF5F7F9)
    val ROADS = Color(0xFFE0E0E0)
    val HIGHWAYS = Color(0xFFF2C078)
    val WATER = Color(0xFFD6EDF7)
    val PARKS = Color(0xFFE8F5E9)
    val LABELS = Color(0xFF5E6B73)
    val ROUTE_ACCENT = Color(0xFF2F8CFF)
    val USER_LOCATION_BLUE = Color(0xFF4285F4)
    val USER_LOCATION_WHITE = Color(0xFFFFFFFF)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GoogleMapsStyleMapScreen(
    buses: List<Bus>,
    selectedBus: Bus?,
    onBusSelected: (Bus) -> Unit,
    shouldCenterOnUser: Boolean = false,
    onLocationCentered: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val locationHelper = remember { LocationHelper(context) }
    
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var pointAnnotationManager by remember { mutableStateOf<PointAnnotationManager?>(null) }
    var userLocation by remember { mutableStateOf<Point?>(null) }
    var isMapReady by remember { mutableStateOf(false) }
    var isLocationComponentEnabled by remember { mutableStateOf(false) }
    var userAccuracy by remember { mutableStateOf(0f) }
    
    // Request location permission on first launch
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }
    
    // Get user location using existing LocationHelper
    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            try {
                Log.d(TAG, "Getting user location...")
                val location = locationHelper.getCurrentLocation()
                userLocation = location
                userAccuracy = 50f // Default accuracy in meters
                Log.d(TAG, "User location obtained: $location")
            } catch (e: Exception) {
                Log.e(TAG, "Error getting user location", e)
                userLocation = locationHelper.getDefaultLocation()
            }
        } else {
            Log.d(TAG, "Location permission not granted, using default location")
            userLocation = locationHelper.getDefaultLocation()
        }
    }
    
    // Center map on user location when requested
    LaunchedEffect(shouldCenterOnUser) {
        Log.d(TAG, "shouldCenterOnUser changed: $shouldCenterOnUser")
        Log.d(TAG, "userLocation: $userLocation")
        Log.d(TAG, "mapView: $mapView")
        Log.d(TAG, "isMapReady: $isMapReady")
        
        if (shouldCenterOnUser && userLocation != null && mapView != null && isMapReady) {
            try {
                Log.d(TAG, "Centering map on user location: $userLocation")
                val mapboxMap = mapView?.getMapboxMap()
                if (mapboxMap != null) {
                    val cameraOptions = CameraOptions.Builder()
                        .center(userLocation)
                        .zoom(16.0)
                        .build()
                    
                    // Use smooth camera animation
                    mapboxMap.setCamera(cameraOptions)
                    Log.d(TAG, "Map centered successfully on user location")
                    onLocationCentered()
                } else {
                    Log.e(TAG, "MapboxMap is null")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error centering map on user location", e)
            }
        } else {
            Log.d(TAG, "Cannot center map - conditions not met")
        }
    }
    
    // Update bus markers when buses change
    LaunchedEffect(buses, selectedBus) {
        if (isMapReady && pointAnnotationManager != null) {
            // updateBusMarkers(pointAnnotationManager!!, buses, selectedBus, onBusSelected)
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Mapbox Map with Google Maps-like style
        AndroidView(
            factory = { ctx ->
                val options = MapInitOptions(ctx)
                val view = MapView(ctx, options)
                mapView = view
                
                // Load custom Google Maps-like style
                view.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
                    // Enable location component with Google Maps-like styling
                    view.location.updateSettings {
                        enabled = true
                        pulsingEnabled = false // We'll create our own pulsing effect
                    }
                    
                    // Create point annotation manager for bus markers
                    pointAnnotationManager = view.annotations.createPointAnnotationManager()
                    
                    // Set initial camera position
                    val initialLocation = userLocation ?: locationHelper.getDefaultLocation()
                    val cameraOptions = CameraOptions.Builder()
                        .center(initialLocation)
                        .zoom(14.0)
                        .build()
                    
                    view.getMapboxMap().setCamera(cameraOptions)
                    isMapReady = true
                    isLocationComponentEnabled = true
                }
                
                view
            },
            modifier = Modifier.fillMaxSize()
        )
        
        
        // Bus clustering info overlay (when zoomed out)
        if (buses.size > 5) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 120.dp, end = 16.dp)
                    .zIndex(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${buses.size} buses",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
    
    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            try {
                Log.d(TAG, "Disposing MapView...")
                mapView?.let { view ->
                    try {
                        view.onDestroy()
                        Log.d(TAG, "MapView disposed successfully")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error during MapView destruction", e)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error disposing MapView", e)
            }
        }
    }
}

@Composable
private fun GoogleMapsUserLocationPuck(
    modifier: Modifier = Modifier
) {
    // Pulsing animation for the accuracy ring
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    
    Box(modifier = modifier) {
        // Accuracy ring (pulsing)
        Box(
            modifier = Modifier
                .size((50 * pulseScale).dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    GoogleMapsColors.USER_LOCATION_BLUE.copy(alpha = pulseAlpha)
                )
        )
        
        // White border ring
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(GoogleMapsColors.USER_LOCATION_WHITE)
        )
        
        // Blue center dot
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(GoogleMapsColors.USER_LOCATION_BLUE)
        )
    }
}

@Composable
private fun updateBusMarkers(
    pointAnnotationManager: PointAnnotationManager,
    buses: List<Bus>,
    selectedBus: Bus?,
    onBusSelected: (Bus) -> Unit
) {
    // Clear existing markers
    pointAnnotationManager.deleteAll()
    
    // Add new bus markers
    buses.forEach { bus ->
        try {
            val point = Point.fromLngLat(bus.longitude, bus.latitude)
            val annotationOptions = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage("bus_marker")
                .withIconSize(if (selectedBus?.id == bus.id) 2.0 else 1.5)
                .withIconColor(if (selectedBus?.id == bus.id) 
                    GoogleMapsColors.ROUTE_ACCENT.toArgb() 
                else 
                    GoogleMapsColors.USER_LOCATION_BLUE.toArgb())
            
            val annotation = pointAnnotationManager.create(annotationOptions)
            
            // Note: Click listener would be added here in a full implementation
        } catch (e: Exception) {
            Log.e(TAG, "Error creating bus marker for ${bus.id}", e)
        }
    }
}

// Extension function to customize Mapbox style for Google Maps look
// This will be implemented when we have proper Mapbox style customization
private fun Style.customizeStyleForGoogleMaps() {
    // For now, we'll use the default Mapbox style
    // Custom styling will be implemented in a future update
}
