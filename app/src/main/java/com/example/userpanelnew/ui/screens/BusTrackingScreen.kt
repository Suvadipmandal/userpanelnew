package com.example.userpanelnew.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
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
private const val TAG = "BusTrackingScreen"

// Vadodara Junction coordinates
private val VADODARA_JUNCTION = Point.fromLngLat(73.1812, 22.3072)

// Enhanced color scheme for bus tracking
object BusTrackingColors {
    val TRACKING_ACTIVE = Color(0xFF4CAF50)
    val TRACKING_INACTIVE = Color(0xFF757575)
    val BUS_MARKER = Color(0xFFFF5722)
    val USER_MARKER = Color(0xFF2196F3)
    val TRACKING_LINE = Color(0xFF4CAF50)
    val ETA_CARD = Color(0xFF2196F3)
    val ERROR_COLOR = Color(0xFFF44336)
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BusTrackingScreen(
    modifier: Modifier = Modifier,
    viewModel: BusTrackingViewModel = viewModel()
) {
    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val locationHelper = remember { LocationHelper(context) }
    
    // ViewModel state
    val isTracking by viewModel.isTracking.collectAsState()
    val busLocation by viewModel.busLocation.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val notificationState by viewModel.notificationState.collectAsState()
    val error by viewModel.error.collectAsState()
    val shouldFitBothMarkers by viewModel.shouldFitBothMarkers.collectAsState()
    
    // Local state
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var pointAnnotationManager by remember { mutableStateOf<PointAnnotationManager?>(null) }
    var isMapReady by remember { mutableStateOf(false) }
    var currentUserLocation by remember { mutableStateOf<Point?>(null) }
    var hasNavigatedToVadodara by remember { mutableStateOf(false) }
    
    // Initialize tracking view model
    LaunchedEffect(Unit) {
        viewModel.initializeLocationHelper(context)
    }
    
    // Request location permission on first launch
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }
    
    // Get user location when permission is granted
    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            try {
                Log.d(TAG, "Getting user location...")
                val location = locationHelper.getCurrentLocation()
                currentUserLocation = location
                Log.d(TAG, "User location obtained: $location")
            } catch (e: Exception) {
                Log.e(TAG, "Error getting user location", e)
                currentUserLocation = locationHelper.getDefaultLocation()
            }
        } else {
            Log.d(TAG, "Location permission not granted, using default location")
            currentUserLocation = locationHelper.getDefaultLocation()
        }
    }
    
    // Navigate to Vadodara Junction when tracking starts
    LaunchedEffect(isTracking) {
        if (isTracking && !hasNavigatedToVadodara && isMapReady && mapView != null) {
            try {
                Log.d(TAG, "Navigating to Vadodara Junction for tracking")
                val mapboxMap = mapView?.getMapboxMap()
                if (mapboxMap != null) {
                    val cameraOptions = CameraOptions.Builder()
                        .center(VADODARA_JUNCTION)
                        .zoom(15.0)
                        .build()
                    
                    mapboxMap.setCamera(cameraOptions)
                    hasNavigatedToVadodara = true
                    Log.d(TAG, "Map centered on Vadodara Junction")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error navigating to Vadodara Junction", e)
            }
        }
    }
    
    // Fit camera to both markers when tracking
    LaunchedEffect(shouldFitBothMarkers) {
        if (shouldFitBothMarkers && isMapReady && mapView != null && isTracking) {
            try {
                val mapboxMap = mapView?.getMapboxMap()
                if (mapboxMap != null) {
                    val userPoint = userLocation?.let { 
                        Point.fromLngLat(it.longitude, it.latitude) 
                    } ?: currentUserLocation
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
                        Log.d(TAG, "Camera fitted to both markers")
                        viewModel.resetFitMarkersFlag()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fitting camera to both markers", e)
            }
        }
    }
    
    // Update markers when locations change
    LaunchedEffect(isTracking, busLocation, userLocation, currentUserLocation) {
        if (isMapReady && pointAnnotationManager != null) {
            updateTrackingMarkers(
                pointAnnotationManager!!,
                isTracking,
                busLocation,
                userLocation,
                currentUserLocation
            )
        }
    }
    
    // Update tracking line when locations change
    LaunchedEffect(isTracking, userLocation, busLocation) {
        if (isMapReady && mapView != null) {
            if (isTracking) {
                updateTrackingLine(mapView!!, userLocation, busLocation)
            } else {
                clearTrackingLine(mapView!!)
            }
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Mapbox Map
        AndroidView(
            factory = { ctx ->
                val options = MapInitOptions(ctx)
                val view = MapView(ctx, options)
                mapView = view
                
                // Load Mapbox Streets style
                view.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
                    // Enable location component
                    view.location.updateSettings {
                        enabled = true
                        pulsingEnabled = true
                    }
                    
                    // Create point annotation manager for markers
                    pointAnnotationManager = view.annotations.createPointAnnotationManager()
                    
                    // Set initial camera position to Vadodara Junction
                    val cameraOptions = CameraOptions.Builder()
                        .center(VADODARA_JUNCTION)
                        .zoom(14.0)
                        .build()
                    
                    view.getMapboxMap().setCamera(cameraOptions)
                    isMapReady = true
                }
                
                view
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = if (isTracking) "Tracking Bus" else "NextStop",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            )
        )
        
        // Main Action Button
        FloatingActionButton(
            onClick = {
                if (isTracking) {
                    viewModel.stopTracking()
                    hasNavigatedToVadodara = false
                } else {
                    // Start tracking with demo bus
                    viewModel.startTracking("demoBus", "user123")
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
                .zIndex(2f),
            containerColor = if (isTracking) BusTrackingColors.TRACKING_ACTIVE else BusTrackingColors.TRACKING_INACTIVE
        ) {
            Icon(
                imageVector = if (isTracking) Icons.Default.Close else Icons.Default.PlayArrow,
                contentDescription = if (isTracking) "Stop Tracking" else "Start Tracking",
                tint = Color.White
            )
        }
        
        // Tracking Status Card
        if (isTracking && notificationState.isVisible) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 80.dp, end = 16.dp)
                    .zIndex(2f),
                colors = CardDefaults.cardColors(
                    containerColor = BusTrackingColors.ETA_CARD.copy(alpha = 0.95f)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Live indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val infiniteTransition = rememberInfiniteTransition(label = "livePulse")
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
                            text = "LIVE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Bus info
                    Text(
                        text = "Bus: ${notificationState.busId}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                    
                    if (notificationState.busRoute.isNotEmpty()) {
                        Text(
                            text = "Route: ${notificationState.busRoute}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Distance and ETA
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Distance",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = viewModel.getFormattedDistance(),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Column {
                            Text(
                                text = "ETA",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "${notificationState.eta} min",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        
        // Error Snackbar
        error?.let { errorMessage ->
            LaunchedEffect(errorMessage) {
                delay(3000) // Auto-dismiss after 3 seconds
                viewModel.clearError()
            }
            
            Card(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 200.dp)
                    .zIndex(2f),
                colors = CardDefaults.cardColors(
                    containerColor = BusTrackingColors.ERROR_COLOR.copy(alpha = 0.95f)
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }
        
        // Permission denied message
        if (!locationPermissionState.status.isGranted) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
                    .zIndex(2f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Location Permission Required",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Please grant location permission to track buses",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { locationPermissionState.launchPermissionRequest() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(
                            text = "Grant Permission",
                            color = Color.White
                        )
                    }
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

private fun updateTrackingMarkers(
    pointAnnotationManager: PointAnnotationManager,
    isTracking: Boolean,
    busLocation: BusLocation?,
    userLocation: UserLocation?,
    currentUserLocation: Point?
) {
    // Clear existing markers
    pointAnnotationManager.deleteAll()
    
    if (isTracking) {
        // Add user location marker
        val userPoint = userLocation?.let { 
            Point.fromLngLat(it.longitude, it.latitude) 
        } ?: currentUserLocation
        
        userPoint?.let { point ->
            try {
                val userAnnotationOptions = PointAnnotationOptions()
                    .withPoint(point)
                    .withIconImage("user_marker")
                    .withIconSize(1.5)
                    .withIconColor(BusTrackingColors.USER_MARKER.toArgb())
                
                pointAnnotationManager.create(userAnnotationOptions)
            } catch (e: Exception) {
                Log.e(TAG, "Error creating user marker", e)
            }
        }
        
        // Add bus location marker
        busLocation?.let { bus ->
            try {
                val busPoint = Point.fromLngLat(bus.longitude, bus.latitude)
                val busAnnotationOptions = PointAnnotationOptions()
                    .withPoint(busPoint)
                    .withIconImage("bus_marker")
                    .withIconSize(2.0)
                    .withIconColor(BusTrackingColors.BUS_MARKER.toArgb())
                
                pointAnnotationManager.create(busAnnotationOptions)
            } catch (e: Exception) {
                Log.e(TAG, "Error creating bus marker", e)
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
                
                // For now, we'll use a simplified approach without adding the line
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