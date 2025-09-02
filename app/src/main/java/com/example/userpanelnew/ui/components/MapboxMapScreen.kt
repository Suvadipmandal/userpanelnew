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
import com.example.userpanelnew.models.Bus
import com.example.userpanelnew.utils.LocationHelper
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style

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
            if (locationHelper.hasLocationPermission()) {
                val location = locationHelper.getCurrentLocation()
                userLocation = location
            } else {
                userLocation = locationHelper.getDefaultLocation()
            }
        } catch (e: Exception) {
            userLocation = locationHelper.getDefaultLocation()
        }
    }
    
    // Center map on user location when requested
    LaunchedEffect(shouldCenterOnUser) {
        if (shouldCenterOnUser && userLocation != null && mapView != null && isMapReady) {
            try {
                mapView?.getMapboxMap()?.setCamera(
                    com.mapbox.maps.CameraOptions.Builder()
                        .center(userLocation)
                        .zoom(15.0)
                        .build()
                )
                onLocationCentered()
            } catch (e: Exception) {
                // Handle camera error gracefully
            }
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            try {
                mapView?.onDestroy()
            } catch (e: Exception) {
                // Handle cleanup error gracefully
            }
        }
    }
    
    // Load Mapbox map
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                mapView = this
                getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
                    try {
                        val initialLocation = userLocation ?: locationHelper.getDefaultLocation()
                        getMapboxMap().setCamera(
                            com.mapbox.maps.CameraOptions.Builder()
                                .center(initialLocation)
                                .zoom(12.0)
                                .build()
                        )
                        isMapReady = true
                    } catch (e: Exception) {
                        isMapReady = true
                    }
                }
            }
        },
        modifier = modifier
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
