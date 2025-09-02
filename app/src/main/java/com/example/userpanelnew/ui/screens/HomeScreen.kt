package com.example.userpanelnew.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.userpanelnew.models.Bus
import com.example.userpanelnew.ui.components.BusBottomSheet
import com.example.userpanelnew.ui.components.MapboxMapScreen
import com.example.userpanelnew.utils.LocationHelper
import com.example.userpanelnew.viewmodels.MainViewModel
import com.mapbox.geojson.Point
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val buses by viewModel.buses.collectAsState()
    val selectedBus by viewModel.selectedBus.collectAsState()
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }
    val scope = rememberCoroutineScope()
    
    var shouldCenterOnUser by remember { mutableStateOf(false) }
    
    Box(modifier = modifier.fillMaxSize()) {
        // ACTUAL MAPBOX MAP - Replace placeholder
        MapboxMapScreen(
            buses = buses,
            selectedBus = selectedBus,
            onBusSelected = { bus -> viewModel.selectBus(bus) },
            shouldCenterOnUser = shouldCenterOnUser,
            onLocationCentered = { shouldCenterOnUser = false },
            modifier = Modifier.fillMaxSize()
        )
        
        // FAB for refresh
        FloatingActionButton(
            onClick = { viewModel.refreshBuses() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
        }
        
        // Location FAB - Now functional
        FloatingActionButton(
            onClick = { 
                scope.launch {
                    val currentLocation = locationHelper.getCurrentLocation()
                    if (currentLocation != null) {
                        shouldCenterOnUser = true
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = "My Location")
        }
        
        // Bus list overlay
        if (buses.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .width(280.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Nearby Buses",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(buses.take(3)) { bus ->
                            BusListItem(
                                bus = bus,
                                onClick = { viewModel.selectBus(bus) }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Bus Bottom Sheet
    selectedBus?.let { bus ->
        BusBottomSheet(
            bus = bus,
            onDismiss = { viewModel.clearSelectedBus() },
            onTrackBus = { /* Track bus functionality */ }
        )
    }
}

@Composable
fun BusListItem(
    bus: Bus,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🚌",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = bus.id,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ETA: ${bus.eta} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
