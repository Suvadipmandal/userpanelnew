package com.example.userpanelnew.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.maps.MapView
import com.mapbox.maps.Style

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapboxScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Remember the MapView to prevent recreation
    var mapView by remember { mutableStateOf<MapView?>(null) }
    
    // Clean up MapView when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            mapView?.onDestroy()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapbox Integration") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        // Mapbox MapView wrapped in AndroidView
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    mapView = this
                    
                    // Load the default Mapbox Streets style using the new API
                    getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
                        // Style loaded successfully
                        // You can add custom styling here if needed
                    }
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}
