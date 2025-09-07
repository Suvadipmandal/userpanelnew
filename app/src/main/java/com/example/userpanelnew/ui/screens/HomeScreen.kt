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
import com.example.userpanelnew.ui.components.*
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
        // Google Maps-style Mapbox Map with enhanced features
        GoogleMapsStyleMapScreen(
            buses = filteredBuses,
            selectedBus = selectedBus,
            onBusSelected = { bus -> viewModel.selectBus(bus) },
            shouldCenterOnUser = shouldCenterOnUser,
            onLocationCentered = { shouldCenterOnUser = false },
            modifier = Modifier.fillMaxSize()
        )
        
        // Enhanced search bar with Material3 styling
        GoogleMapsSearchBarFixed(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onClearQuery = { searchQuery = "" },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                .zIndex(1f)
        )
        
        // Action buttons (bottom-right)
        ActionButtonGroup(
            onMyLocationClick = { shouldCenterOnUser = true },
            onRefreshClick = { 
                viewModel.refreshBuses()
                viewModel.refreshBusStops()
            },
            isLocationEnabled = true,
            isRefreshing = false,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .zIndex(1f)
        )
        
    }
    
    // Enhanced Bus Bottom Sheet
    selectedBus?.let { bus ->
        EnhancedBusBottomSheet(
            bus = bus,
            onDismiss = { viewModel.clearSelectedBus() },
            onTrackBus = { /* Track bus functionality */ }
        )
    }
}
