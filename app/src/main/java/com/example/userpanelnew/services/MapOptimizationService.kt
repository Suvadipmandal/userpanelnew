package com.example.userpanelnew.services

import android.content.Context
import android.util.Log
import com.example.userpanelnew.models.Bus
import com.mapbox.geojson.Point
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap

/**
 * Service for optimizing map performance and reducing bandwidth usage
 */
class MapOptimizationService(private val context: Context) {
    
    companion object {
        private const val TAG = "MapOptimizationService"
        private const val CACHE_SIZE_LIMIT = 100
        private const val UPDATE_INTERVAL_MS = 5000L // 5 seconds
        private const val MAX_BUS_DISTANCE_KM = 10.0 // Only show buses within 10km
    }
    
    // Cache for bus locations
    private val busLocationCache = ConcurrentHashMap<String, CachedBusLocation>()
    
    // Cache for map tiles
    private val mapTileCache = ConcurrentHashMap<String, Long>()
    
    // Filtered buses based on user location
    private val _filteredBuses = MutableStateFlow<List<Bus>>(emptyList())
    val filteredBuses: StateFlow<List<Bus>> = _filteredBuses.asStateFlow()
    
    // User location for filtering
    private var userLocation: Point? = null
    
    // Coroutine scope for background operations
    private val optimizationScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    /**
     * Update user location and filter buses accordingly
     */
    fun updateUserLocation(location: Point) {
        userLocation = location
        filterBusesByDistance()
    }
    
    /**
     * Add or update bus location in cache
     */
    fun updateBusLocation(bus: Bus) {
        val currentTime = System.currentTimeMillis()
        val cachedLocation = CachedBusLocation(
            bus = bus,
            timestamp = currentTime,
            point = Point.fromLngLat(bus.longitude, bus.latitude)
        )
        
        busLocationCache[bus.id] = cachedLocation
        
        // Clean up old cache entries
        cleanupCache()
        
        // Filter buses if user location is available
        if (userLocation != null) {
            filterBusesByDistance()
        }
    }
    
    /**
     * Get cached bus locations
     */
    fun getCachedBusLocations(): List<Bus> {
        val currentTime = System.currentTimeMillis()
        return busLocationCache.values
            .filter { currentTime - it.timestamp < UPDATE_INTERVAL_MS * 2 } // Keep for 2 update cycles
            .map { it.bus }
    }
    
    /**
     * Filter buses by distance from user location
     */
    private fun filterBusesByDistance() {
        val userLoc = userLocation ?: return
        val currentTime = System.currentTimeMillis()
        
        val nearbyBuses = busLocationCache.values
            .filter { currentTime - it.timestamp < UPDATE_INTERVAL_MS * 2 }
            .filter { cachedLocation ->
                val distance = calculateDistance(
                    userLoc.latitude(), userLoc.longitude(),
                    cachedLocation.point.latitude(), cachedLocation.point.longitude()
                )
                distance <= MAX_BUS_DISTANCE_KM
            }
            .map { it.bus }
            .sortedBy { bus ->
                calculateDistance(
                    userLoc.latitude(), userLoc.longitude(),
                    bus.latitude, bus.longitude
                )
            }
        
        optimizationScope.launch {
            _filteredBuses.emit(nearbyBuses)
        }
    }
    
    /**
     * Calculate distance between two points in kilometers
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // Earth's radius in kilometers
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
        
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        
        return earthRadius * c
    }
    
    /**
     * Clean up old cache entries
     */
    private fun cleanupCache() {
        val currentTime = System.currentTimeMillis()
        val maxAge = UPDATE_INTERVAL_MS * 3 // Keep for 3 update cycles
        
        // Remove old bus locations
        val iterator = busLocationCache.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (currentTime - entry.value.timestamp > maxAge) {
                iterator.remove()
            }
        }
        
        // Limit cache size
        if (busLocationCache.size > CACHE_SIZE_LIMIT) {
            val sortedEntries = busLocationCache.entries.sortedBy { it.value.timestamp }
            val entriesToRemove = sortedEntries.take(busLocationCache.size - CACHE_SIZE_LIMIT)
            entriesToRemove.forEach { busLocationCache.remove(it.key) }
        }
        
        // Clean up map tile cache
        val tileIterator = mapTileCache.iterator()
        while (tileIterator.hasNext()) {
            val entry = tileIterator.next()
            if (currentTime - entry.value > maxAge) {
                tileIterator.remove()
            }
        }
    }
    
    /**
     * Get optimized map style configuration
     */
    fun getOptimizedMapStyle(): MapStyleConfig {
        return MapStyleConfig(
            useCompressedTiles = true,
            reduceLabelDensity = true,
            simplifyRoads = true,
            reduceBuildingDetails = true
        )
    }
    
    /**
     * Check if network is available and adjust optimization accordingly
     */
    fun adjustOptimizationForNetwork(isLowBandwidth: Boolean) {
        if (isLowBandwidth) {
            Log.d(TAG, "Low bandwidth detected, enabling aggressive optimization")
            // Reduce update frequency
            // Use more aggressive caching
            // Simplify map rendering
        } else {
            Log.d(TAG, "Good network connection, using standard optimization")
        }
    }
    
    /**
     * Clear all caches
     */
    fun clearCache() {
        busLocationCache.clear()
        mapTileCache.clear()
        optimizationScope.launch {
            _filteredBuses.emit(emptyList())
        }
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        optimizationScope.cancel()
        clearCache()
    }
}

/**
 * Data class for cached bus location
 */
data class CachedBusLocation(
    val bus: Bus,
    val timestamp: Long,
    val point: Point
)

/**
 * Configuration for optimized map style
 */
data class MapStyleConfig(
    val useCompressedTiles: Boolean = true,
    val reduceLabelDensity: Boolean = true,
    val simplifyRoads: Boolean = true,
    val reduceBuildingDetails: Boolean = true
)
