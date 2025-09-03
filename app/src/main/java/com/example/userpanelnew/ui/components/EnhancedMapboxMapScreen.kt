package com.example.userpanelnew.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import android.util.Log
import com.example.userpanelnew.models.Bus
import com.example.userpanelnew.utils.LocationHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.viewport
import kotlinx.coroutines.delay

// Logging tag
private const val TAG = "EnhancedMapboxMapScreen"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EnhancedMapboxMapScreen(
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
                val location = locationHelper.getCurrentLocation()
                userLocation = location
            } catch (e: Exception) {
                Log.e(TAG, "Error getting user location", e)
                userLocation = locationHelper.getDefaultLocation()
            }
        }
    }
    
    // Center map on user location when requested
    LaunchedEffect(shouldCenterOnUser) {
        if (shouldCenterOnUser && userLocation != null && mapView != null && isMapReady) {
            try {
                Log.d(TAG, "Centering map on user location...")
                val mapboxMap = mapView?.getMapboxMap()
                if (mapboxMap != null) {
                    val cameraOptions = CameraOptions.Builder()
                        .center(userLocation)
                        .zoom(16.0)
                        .build()
                    
                    mapboxMap.setCamera(cameraOptions)
                    Log.d(TAG, "Map centered successfully on user location")
                    onLocationCentered()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error centering map on user location", e)
            }
        }
    }
    
            // Update bus markers when buses change
        LaunchedEffect(buses, selectedBus) {
            if (isMapReady && pointAnnotationManager != null) {
                // For now, we'll handle markers differently to avoid API issues
                // updateBusMarkers(pointAnnotationManager!!, buses, selectedBus, onBusSelected)
            }
        }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Mapbox Map
        AndroidView(
            factory = { ctx ->
                val options = MapInitOptions(ctx)
                val view = MapView(ctx, options)
                mapView = view
                
                view.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
                    // Enable location component
                    view.location.updateSettings {
                        enabled = true
                        pulsingEnabled = true
                        pulsingMaxRadius = 20.0f
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
        
        // User location indicator (pulsing blue dot overlay)
        if (userLocation != null && isLocationComponentEnabled) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(24.dp)
                    .zIndex(2f)
            ) {
                // Pulsing animation effect
                var pulseScale by remember { mutableStateOf(1f) }
                var pulseAlpha by remember { mutableStateOf(0.7f) }
                
                LaunchedEffect(Unit) {
                    while (true) {
                        delay(1000)
                        pulseScale = 1.2f
                        pulseAlpha = 0.3f
                        delay(1000)
                        pulseScale = 1f
                        pulseAlpha = 0.7f
                    }
                }
                
                // Main blue dot
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2196F3))
                )
                
                // Pulsing ring
                Box(
                    modifier = Modifier
                        .size((16 * pulseScale).dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2196F3).copy(alpha = pulseAlpha))
                )
            }
        }
        
        // Bus clustering info overlay (when zoomed out)
        if (buses.size > 5) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 120.dp, end = 16.dp)
                    .zIndex(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                        color = MaterialTheme.colorScheme.onSurface
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

// Bus markers will be implemented in a future update
// For now, we'll use the basic map functionality
