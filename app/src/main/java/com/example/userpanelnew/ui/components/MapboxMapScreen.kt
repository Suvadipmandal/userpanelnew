package com.example.userpanelnew.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import android.util.Log
import com.example.userpanelnew.models.Bus
import com.example.userpanelnew.utils.LocationHelper
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.Style


// Logging tag
private const val TAG = "MapboxMapScreen"

@Composable
fun MapboxMapScreen(
    buses: List<Bus>,
    selectedBus: Bus?,
    onBusSelected: (Bus) -> Unit,
    shouldCenterOnUser: Boolean = false,
    onLocationCentered: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }
    
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var userLocation by remember { mutableStateOf<Point?>(null) }
    var isMapReady by remember { mutableStateOf(false) }
    
    // Get user location on first load
    LaunchedEffect(Unit) {
        try {
            Log.d(TAG, "Getting user location...")
            if (locationHelper.hasLocationPermission()) {
                Log.d(TAG, "Location permission granted, getting current location...")
                val location = locationHelper.getCurrentLocation()
                userLocation = location
                Log.d(TAG, "Current location obtained: $location")
            } else {
                Log.d(TAG, "Location permission not granted, using default location...")
                userLocation = locationHelper.getDefaultLocation()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user location", e)
            try {
                userLocation = locationHelper.getDefaultLocation()
            } catch (fallbackException: Exception) {
                Log.e(TAG, "Error getting fallback location", fallbackException)
                // Use a hardcoded fallback location
                try {
                    userLocation = Point.fromLngLat(0.0, 0.0)
                } catch (pointException: Exception) {
                    Log.e(TAG, "Error creating hardcoded fallback point", pointException)
                    // If all else fails, don't set userLocation
                }
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
                    try {
                        val cameraOptions = try {
                            com.mapbox.maps.CameraOptions.Builder()
                                .center(userLocation)
                                .zoom(15.0)
                                .build()
                        } catch (e: Exception) {
                            Log.e(TAG, "Error building camera options", e)
                            return@LaunchedEffect
                        }
                        
                        try {
                            mapboxMap.setCamera(cameraOptions)
                            Log.d(TAG, "Map centered successfully on user location")
                            onLocationCentered()
                        } catch (cameraException: Exception) {
                            Log.e(TAG, "Error setting camera position", cameraException)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error setting camera position", e)
                    }
                } else {
                    Log.w(TAG, "MapboxMap is null or MapView is destroyed, cannot center camera")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error centering map on user location", e)
                // Handle camera error gracefully
            }
        }
    }
    
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
                // Handle cleanup error gracefully
            }
        }
    }
    
    // Load Mapbox map
    AndroidView(
        factory = { context ->
            try {
                Log.d(TAG, "Creating MapInitOptions...")
                val mapInitOptions = try {
                    MapInitOptions(context)
                } catch (e: Exception) {
                    Log.e(TAG, "Error creating MapInitOptions", e)
                    throw e
                }
                
                Log.d(TAG, "Creating MapView with MapInitOptions...")
                val view = try {
                    MapView(context, mapInitOptions)
                } catch (e: Exception) {
                    Log.e(TAG, "Error creating MapView", e)
                    throw e
                }
                
                mapView = view
                Log.d(TAG, "MapView created successfully, loading style...")
                
                // Load style and set camera asynchronously
                try {
                    val mapboxMap = try {
                        view.getMapboxMap()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error getting MapboxMap", e)
                        throw e
                    }
                    
                    mapboxMap.loadStyleUri(Style.MAPBOX_STREETS) { style ->
                        try {
                            Log.d(TAG, "Style loaded successfully, setting camera...")
                            val initialLocation = userLocation ?: try {
                                locationHelper.getDefaultLocation()
                            } catch (e: Exception) {
                                Log.e(TAG, "Error getting default location for camera", e)
                                Point.fromLngLat(0.0, 0.0)
                            }
                            
                            try {
                                val cameraOptions = try {
                                    com.mapbox.maps.CameraOptions.Builder()
                                        .center(initialLocation)
                                        .zoom(12.0)
                                        .build()
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error building initial camera options", e)
                                    return@loadStyleUri
                                }
                                
                                mapboxMap.setCamera(cameraOptions)
                                Log.d(TAG, "Camera set successfully")
                            } catch (cameraException: Exception) {
                                Log.e(TAG, "Error setting camera", cameraException)
                            }
                            
                            isMapReady = true
                        } catch (e: Exception) {
                            Log.e(TAG, "Mapbox camera error", e)
                            isMapReady = true
                        }
                    }
                } catch (styleException: Exception) {
                    Log.e(TAG, "Error loading Mapbox style", styleException)
                    isMapReady = true
                }
                
                view
            } catch (e: Exception) {
                Log.e(TAG, "Mapbox initialization error", e)
                // Return a fallback view if Mapbox fails
                android.widget.TextView(context).apply {
                    text = "Map loading failed: ${e.message}"
                    setTextColor(android.graphics.Color.RED)
                    setPadding(16, 16, 16, 16)
                }
            }
        },

        modifier = modifier.fillMaxSize()
    )
    
    // Bus markers overlay
    buses.forEachIndexed { index, bus ->
        val x = (100 + index * 80).dp
        val y = (200 + index * 60).dp
        
        Box(
            modifier = Modifier
                .offset(x = x, y = y)
                .size(40.dp)
                .background(
                    color = if (bus.id == selectedBus?.id) Color.Red else Color(0xFFFF5722),
                    shape = CircleShape
                )
                .clickable { onBusSelected(bus) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🚌",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 20.sp
            )
        }
    }
    
    // User location indicator
    userLocation?.let { location ->
        Box(
            modifier = Modifier
                .offset(x = 16.dp, y = 16.dp)
                .size(16.dp)
                .background(
                    color = Color(0xFF2196F2),
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun BusMarkerView(
    bus: Bus,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                color = if (isSelected) Color.Red else Color(0xFFFF5722),
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text("🚌", fontSize = 20.sp)
    }
}
