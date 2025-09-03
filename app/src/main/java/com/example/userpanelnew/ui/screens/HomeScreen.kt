package com.example.userpanelnew.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
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
    var searchQuery by remember { mutableStateOf("") }
    
    // Filter buses based on search query
    val filteredBuses = remember(buses, searchQuery) {
        if (searchQuery.isEmpty()) {
            buses
        } else {
            buses.filter { bus ->
                bus.id.contains(searchQuery, ignoreCase = true) ||
                bus.route.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // ACTUAL MAPBOX MAP - Replace placeholder

        MapboxMapScreen(
            buses = filteredBuses,
            selectedBus = selectedBus,
            onBusSelected = { bus -> viewModel.selectBus(bus) },
            shouldCenterOnUser = shouldCenterOnUser,
            onLocationCentered = { shouldCenterOnUser = false },
            modifier = Modifier.fillMaxSize()
        )
        
        // Compact and Elegant Search Bar
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp, start = 24.dp, end = 24.dp)
                .fillMaxWidth(0.78f)
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.05f),
                    RoundedCornerShape(28.dp)
                ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
            ),
            shape = RoundedCornerShape(28.dp),
            border = androidx.compose.foundation.BorderStroke(
                width = 0.8.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.06f)
            )
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { 
                    Text(
                        "Search for bus or stop...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    ) 
                },
                leadingIcon = { 
                    Icon(
                        Icons.Default.Search, 
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        modifier = Modifier.size(22.dp)
                    ) 
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(20.dp)
            )
        }
        
        // Enhanced FAB for refresh
        FloatingActionButton(
            onClick = { viewModel.refreshBuses() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 24.dp, end = 24.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
                            Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier.size(24.dp)
                )
        }
        
        // Enhanced Logout FAB
        FloatingActionButton(
            onClick = { viewModel.logout() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 24.dp, start = 24.dp),
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
                            Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout",
                    modifier = Modifier.size(24.dp)
                )
        }
        
        // Enhanced Location FAB
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
                .padding(20.dp),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Icon(
                Icons.Default.LocationOn, 
                contentDescription = "My Location",
                modifier = Modifier.size(24.dp)
            )
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
