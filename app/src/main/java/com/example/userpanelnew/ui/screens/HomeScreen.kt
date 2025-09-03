package com.example.userpanelnew.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.userpanelnew.models.Bus
import com.example.userpanelnew.ui.components.BusBottomSheet
import com.example.userpanelnew.ui.components.EnhancedMapboxMapScreen
import com.example.userpanelnew.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val buses by viewModel.buses.collectAsState()
    val busStops by viewModel.busStops.collectAsState()
    val selectedBus by viewModel.selectedBus.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var shouldCenterOnUser by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    // Filter buses and bus stops based on search query
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
    
    val filteredBusStops = remember(busStops, searchQuery) {
        if (searchQuery.isEmpty()) {
            busStops
        } else {
            busStops.filter { busStop ->
                busStop.id.contains(searchQuery, ignoreCase = true) ||
                busStop.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    // Combined search results
    val hasSearchResults = filteredBuses.isNotEmpty() || filteredBusStops.isNotEmpty()
    val totalSearchResults = filteredBuses.size + filteredBusStops.size
    
    Box(modifier = modifier.fillMaxSize()) {
        // Enhanced Mapbox Map with clustering and better markers
        EnhancedMapboxMapScreen(
            buses = filteredBuses,
            selectedBus = selectedBus,
            onBusSelected = { bus -> viewModel.selectBus(bus) },
            shouldCenterOnUser = shouldCenterOnUser,
            onLocationCentered = { shouldCenterOnUser = false },
            modifier = Modifier.fillMaxSize()
        )
        
        // Top overlay section with search bar and action buttons
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                .zIndex(1f)
        ) {
            // Search bar and action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Compact, rounded search bar with semi-transparent background
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { 
                            Text(
                                "Search for bus or stop...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            ) 
                        },
                        leadingIcon = { 
                            Icon(
                                Icons.Default.Search, 
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            ) 
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchQuery = "" },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Clear search",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
                
                // Top-right action buttons: Refresh and Location Focus
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Refresh Button
                    FloatingActionButton(
                        onClick = { 
                            viewModel.refreshBuses()
                            viewModel.refreshBusStops()
                        },
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 6.dp
                        ),
                        shape = CircleShape,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    // Location Focus Button
                    FloatingActionButton(
                        onClick = { 
                            shouldCenterOnUser = true
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f),
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 6.dp
                        ),
                        shape = CircleShape,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Focus on my location",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            
            // Search results summary (only show when searching)
            if (searchQuery.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (hasSearchResults) {
                                "Found $totalSearchResults results"
                            } else {
                                "No results found"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (hasSearchResults) 
                                MaterialTheme.colorScheme.onSurface 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        // Logout button (top-left, floating)
        FloatingActionButton(
            onClick = { viewModel.logout() },
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f),
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 4.dp,
                pressedElevation = 6.dp
            ),
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 32.dp, start = 16.dp)
                .size(40.dp)
                .zIndex(1f)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Logout",
                modifier = Modifier.size(18.dp)
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
