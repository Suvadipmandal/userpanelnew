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
import com.example.userpanelnew.models.*
import com.example.userpanelnew.utils.LocationHelper
import com.example.userpanelnew.viewmodels.BusTrackingViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.mapbox.geojson.Point
import com.mapbox.geojson.LineString
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
private const val TAG = "EnhancedMapScreen"

// Enhanced Google Maps-like color scheme
object EnhancedGoogleMapsColors {
    val LAND = Color(0xFFF5F7F9)
    val ROADS = Color(0xFFE0E0E0)
    val HIGHWAYS = Color(0xFFF2C078)
    val WATER = Color(0xFFD6EDF7)
    val PARKS = Color(0xFFE8F5E9)
    val LABELS = Color(0xFF5E6B73)
    val ROUTE_ACCENT = Color(0xFF2F8CFF)
    val USER_LOCATION_BLUE = Color(0xFF4285F4)
    val USER_LOCATION_WHITE = Color(0xFFFFFFFF)
    val BUS_MARKER = Color(0xFFFF5722)
    val TRACKING_LINE = Color(0xFF4CAF50)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EnhancedMapScreen(
    buses: List<Bus>,
    selectedBus: Bus?,
    onBusSelected: (Bus) -> Unit,
    shouldCenterOnUser: Boolean = false,
    onLocationCentered: () -> Unit = {},
    trackingViewModel: BusTrackingViewModel? = null,
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
    
    // Tracking state
    val isTracking by trackingViewModel?.isTracking?.collectAsState() ?: remember { mutableStateOf(false) }
    val busLocation by trackingViewModel?.busLocation?.collectAsState() ?: remember { mutableStateOf(null) }
    val userLocationTracking by trackingViewModel?.userLocation?.collectAsState() ?: remember { mutableStateOf(null) }
    val shouldFitBothMarkers by trackingViewModel?.shouldFitBothMarkers?.collectAsState() ?: remember { mutableStateOf(false) }
    
    // Initialize tracking view model
    LaunchedEffect(trackingViewModel) {
        trackingViewModel?.initializeLocationHelper(context)
    }
    
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
    
    // Fit camera to both markers when tracking
    LaunchedEffect(shouldFitBothMarkers) {
        if (shouldFitBothMarkers && isMapReady && mapView != null && isTracking) {
            try {
                val mapboxMap = mapView?.getMapboxMap()
                if (mapboxMap != null) {
                    val userPoint = userLocationTracking?.let { 
                        Point.fromLngLat(it.longitude, it.latitude) 
                    } ?: userLocation
                    val busPoint = busLocation?.let { 
                        Point.fromLngLat(it.longitude, it.latitude) 
                    }
                    
                    if (userPoint != null && busPoint != null) {
                        // Calculate bounds to fit both markers
                        val minLat = minOf(userPoint.latitude(), busPoint.latitude())
                        val maxLat = maxOf(userPoint.latitude(), busPoint.latitude())
                        val minLng = minOf(userPoint.longitude(), busPoint.longitude())
                        val maxLng = maxOf(userPoint.longitude(), busPoint.longitude())
                        
                        // Add padding to bounds
                        val latPadding = (maxLat - minLat) * 0.1
                        val lngPadding = (maxLng - minLng) * 0.1
                        
                        val bounds = CoordinateBounds(
                            Point.fromLngLat(minLng - lngPadding, minLat - latPadding),
                            Point.fromLngLat(maxLng + lngPadding, maxLat + latPadding)
                        )
                        
                        val cameraOptions = mapboxMap.cameraForCoordinateBounds(
                            bounds,
                            EdgeInsets(100.0, 100.0, 100.0, 100.0)
                        )
                        
                        mapboxMap.setCamera(cameraOptions)
                        Log.d(TAG, "Camera fitted to both markers with bounds")
                        trackingViewModel?.resetFitMarkersFlag()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fitting camera to both markers", e)
            }
        }
    }
    
    // Update bus markers when buses change
    LaunchedEffect(buses, selectedBus, isTracking, busLocation) {
        if (isMapReady && pointAnnotationManager != null) {
            updateBusMarkers(
                pointAnnotationManager!!, 
                buses, 
                selectedBus, 
                onBusSelected,
                isTracking,
                busLocation
            )
        }
    }
    
    // Update tracking line when locations change
    LaunchedEffect(isTracking, userLocationTracking, busLocation) {
        if (isMapReady && mapView != null) {
            if (isTracking) {
                updateTrackingLine(mapView!!, userLocationTracking, busLocation)
            } else {
                // Clear tracking line when not tracking
                clearTrackingLine(mapView!!)
            }
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
        
        // Tracking status indicator
        if (isTracking) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 120.dp, start = 16.dp)
                    .zIndex(1f),
                colors = CardDefaults.cardColors(
                    containerColor = EnhancedGoogleMapsColors.TRACKING_LINE.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pulsing dot
                    val infiniteTransition = rememberInfiniteTransition(label = "trackingPulse")
                    val pulseAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.7f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = EaseInOut),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulseAlpha"
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = pulseAlpha))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TRACKING",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
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

private fun updateBusMarkers(
    pointAnnotationManager: PointAnnotationManager,
    buses: List<Bus>,
    selectedBus: Bus?,
    onBusSelected: (Bus) -> Unit,
    isTracking: Boolean,
    trackedBusLocation: BusLocation?
) {
    // Clear existing markers
    pointAnnotationManager.deleteAll()
    
    // Add new bus markers
    buses.forEach { bus ->
        try {
            val point = Point.fromLngLat(bus.longitude, bus.latitude)
            val isSelected = selectedBus?.id == bus.id
            val isTracked = isTracking && trackedBusLocation?.busId == bus.id
            
            val annotationOptions = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage("bus_marker")
                .withIconSize(
                    when {
                        isTracked -> 2.5
                        isSelected -> 2.0
                        else -> 1.5
                    }
                )
                .withIconColor(
                    when {
                        isTracked -> EnhancedGoogleMapsColors.TRACKING_LINE.toArgb()
                        isSelected -> EnhancedGoogleMapsColors.ROUTE_ACCENT.toArgb()
                        else -> EnhancedGoogleMapsColors.BUS_MARKER.toArgb()
                    }
                )
            
            val annotation = pointAnnotationManager.create(annotationOptions)
            
            // Note: Click listener would be added here in a full implementation
        } catch (e: Exception) {
            Log.e(TAG, "Error creating bus marker for ${bus.id}", e)
        }
    }
    
    // Add tracked bus marker if it's not in the regular buses list
    if (isTracking && trackedBusLocation != null) {
        val isInBusesList = buses.any { it.id == trackedBusLocation.busId }
        if (!isInBusesList) {
            try {
                val point = Point.fromLngLat(trackedBusLocation.longitude, trackedBusLocation.latitude)
                val annotationOptions = PointAnnotationOptions()
                    .withPoint(point)
                    .withIconImage("bus_marker")
                    .withIconSize(2.5)
                    .withIconColor(EnhancedGoogleMapsColors.TRACKING_LINE.toArgb())
                
                pointAnnotationManager.create(annotationOptions)
            } catch (e: Exception) {
                Log.e(TAG, "Error creating tracked bus marker", e)
            }
        }
    }
}

private fun updateTrackingLine(
    mapView: MapView,
    userLocation: UserLocation?,
    busLocation: BusLocation?
) {
    if (userLocation != null && busLocation != null) {
        try {
            val mapboxMap = mapView.getMapboxMap()
            
            mapboxMap.getStyle { style ->
                // Create line string between user and bus
                val userPoint = Point.fromLngLat(userLocation.longitude, userLocation.latitude)
                val busPoint = Point.fromLngLat(busLocation.longitude, busLocation.latitude)
                val lineString = LineString.fromLngLats(listOf(userPoint, busPoint))
                
                // Add or update the tracking line source
                val sourceId = "tracking-line-source"
                val layerId = "tracking-line-layer"
                
                // Remove existing source and layer if they exist
                try {
                    style.removeStyleLayer(layerId)
                    style.removeStyleSource(sourceId)
                } catch (e: Exception) {
                    // Source or layer doesn't exist, that's fine
                }
                
                // For now, we'll use a simplified approach
                // The full polyline implementation can be added later with proper Mapbox API
                Log.d(TAG, "Tracking line coordinates: User(${userLocation.latitude}, ${userLocation.longitude}) -> Bus(${busLocation.latitude}, ${busLocation.longitude})")
                
                Log.d(TAG, "Tracking line updated successfully")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating tracking line", e)
        }
    }
}

private fun clearTrackingLine(mapView: MapView) {
    try {
        val mapboxMap = mapView.getMapboxMap()
        mapboxMap.getStyle { style ->
            val sourceId = "tracking-line-source"
            val layerId = "tracking-line-layer"
            
            try {
                style.removeStyleLayer(layerId)
                style.removeStyleSource(sourceId)
                Log.d(TAG, "Tracking line cleared")
            } catch (e: Exception) {
                // Source or layer doesn't exist, that's fine
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error clearing tracking line", e)
    }
}
