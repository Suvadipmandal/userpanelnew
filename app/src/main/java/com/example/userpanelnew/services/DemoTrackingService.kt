package com.example.userpanelnew.services

import android.util.Log
import com.example.userpanelnew.models.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlin.math.*

/**
 * Demo tracking service that provides instant demo data for testing
 * This simulates real-time bus tracking without requiring Firebase setup
 */
class DemoTrackingService {
    
    companion object {
        private const val TAG = "DemoTrackingService"
        private const val UPDATE_INTERVAL = 3000L // 3 seconds
    }
    
    // Demo bus locations that move along a route
    private val demoBusLocations = mutableMapOf<String, BusLocation>()
    private val demoRoutes = mapOf(
        "BUS101" to listOf(
            BusLocation("BUS101", 22.3072, 73.1812, 35.0, 45.0, 10.0, null, "Route 101", true, "DRIVER001", 50, 25),
            BusLocation("BUS101", 22.3080, 73.1820, 38.0, 50.0, 8.0, null, "Route 101", true, "DRIVER001", 50, 28),
            BusLocation("BUS101", 22.3090, 73.1830, 42.0, 55.0, 12.0, null, "Route 101", true, "DRIVER001", 50, 32),
            BusLocation("BUS101", 22.3100, 73.1840, 40.0, 60.0, 9.0, null, "Route 101", true, "DRIVER001", 50, 30),
            BusLocation("BUS101", 22.3110, 73.1850, 45.0, 65.0, 11.0, null, "Route 101", true, "DRIVER001", 50, 35)
        ),
        "BUS102" to listOf(
            BusLocation("BUS102", 22.3090, 73.1850, 28.0, 90.0, 15.0, null, "Route 102", true, "DRIVER002", 45, 30),
            BusLocation("BUS102", 22.3085, 73.1860, 32.0, 95.0, 13.0, null, "Route 102", true, "DRIVER002", 45, 33),
            BusLocation("BUS102", 22.3080, 73.1870, 35.0, 100.0, 10.0, null, "Route 102", true, "DRIVER002", 45, 36),
            BusLocation("BUS102", 22.3075, 73.1880, 30.0, 105.0, 14.0, null, "Route 102", true, "DRIVER002", 45, 31),
            BusLocation("BUS102", 22.3070, 73.1890, 38.0, 110.0, 12.0, null, "Route 102", true, "DRIVER002", 45, 38)
        ),
        "BUS103" to listOf(
            BusLocation("BUS103", 22.3050, 73.1790, 42.0, 180.0, 8.0, null, "Route 103", true, "DRIVER003", 60, 40),
            BusLocation("BUS103", 22.3040, 73.1780, 45.0, 185.0, 9.0, null, "Route 103", true, "DRIVER003", 60, 42),
            BusLocation("BUS103", 22.3030, 73.1770, 48.0, 190.0, 7.0, null, "Route 103", true, "DRIVER003", 60, 45),
            BusLocation("BUS103", 22.3020, 73.1760, 44.0, 195.0, 10.0, null, "Route 103", true, "DRIVER003", 60, 41),
            BusLocation("BUS103", 22.3010, 73.1750, 50.0, 200.0, 6.0, null, "Route 103", true, "DRIVER003", 60, 48)
        ),
        "BUS104" to listOf(
            BusLocation("BUS104", 22.3120, 73.1880, 31.0, 270.0, 12.0, null, "Route 104", true, "DRIVER004", 55, 20),
            BusLocation("BUS104", 22.3130, 73.1870, 34.0, 275.0, 11.0, null, "Route 104", true, "DRIVER004", 55, 23),
            BusLocation("BUS104", 22.3140, 73.1860, 37.0, 280.0, 9.0, null, "Route 104", true, "DRIVER004", 55, 26),
            BusLocation("BUS104", 22.3150, 73.1850, 33.0, 285.0, 13.0, null, "Route 104", true, "DRIVER004", 55, 24),
            BusLocation("BUS104", 22.3160, 73.1840, 40.0, 290.0, 8.0, null, "Route 104", true, "DRIVER004", 55, 29)
        )
    )
    
    private val currentRouteIndex = mutableMapOf<String, Int>()
    
    init {
        // Initialize demo bus locations
        demoRoutes.forEach { (busId, route) ->
            demoBusLocations[busId] = route.first()
            currentRouteIndex[busId] = 0
        }
    }
    
    /**
     * Get real-time bus location updates (demo version)
     */
    fun getBusLocationUpdates(busId: String): Flow<BusLocation?> = flow {
        Log.d(TAG, "Starting demo bus location updates for: $busId")
        
        // Send initial location immediately
        val initialLocation = demoBusLocations[busId]
        if (initialLocation != null) {
            emit(initialLocation)
        }
        
        // Continue sending updates
        while (true) {
            delay(UPDATE_INTERVAL)
            val updatedLocation = getNextBusLocation(busId)
            if (updatedLocation != null) {
                emit(updatedLocation)
            }
        }
    }
    
    /**
     * Get next bus location in the demo route
     */
    private fun getNextBusLocation(busId: String): BusLocation? {
        val route = demoRoutes[busId] ?: return null
        val currentIndex = currentRouteIndex[busId] ?: 0
        
        // Move to next location in route
        val nextIndex = (currentIndex + 1) % route.size
        currentRouteIndex[busId] = nextIndex
        
        val nextLocation = route[nextIndex]
        demoBusLocations[busId] = nextLocation
        
        Log.d(TAG, "Updated demo location for $busId: ${nextLocation.latitude}, ${nextLocation.longitude}")
        return nextLocation
    }
    
    /**
     * Create a demo tracking session
     */
    suspend fun createTrackingSession(
        userId: String,
        busId: String,
        userLocation: UserLocation
    ): Result<TrackingSession> {
        return try {
            Log.d(TAG, "Creating demo tracking session for user: $userId, bus: $busId")
            
            val sessionId = "demo_session_${System.currentTimeMillis()}"
            val session = TrackingSession(
                sessionId = sessionId,
                userId = userId,
                busId = busId,
                startTime = com.google.firebase.Timestamp.now(),
                status = TrackingStatus.ACTIVE,
                userLocation = userLocation
            )
            
            Log.d(TAG, "Demo tracking session created successfully: ${session.sessionId}")
            Result.success(session)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create demo tracking session", e)
            Result.failure(e)
        }
    }
    
    /**
     * Update demo tracking session
     */
    suspend fun updateTrackingSession(
        sessionId: String,
        userLocation: UserLocation,
        busLocation: BusLocation
    ): Result<Unit> {
        return try {
            Log.d(TAG, "Updating demo tracking session: $sessionId")
            
            val distance = calculateDistance(
                userLocation.latitude, userLocation.longitude,
                busLocation.latitude, busLocation.longitude
            )
            
            Log.d(TAG, "Demo tracking session updated - Distance: ${distance}m")
            Result.success(Unit)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update demo tracking session", e)
            Result.failure(e)
        }
    }
    
    /**
     * End demo tracking session
     */
    suspend fun endTrackingSession(sessionId: String): Result<Unit> {
        return try {
            Log.d(TAG, "Ending demo tracking session: $sessionId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to end demo tracking session", e)
            Result.failure(e)
        }
    }
    
    /**
     * Cancel demo tracking session
     */
    suspend fun cancelTrackingSession(sessionId: String): Result<Unit> {
        return try {
            Log.d(TAG, "Cancelling demo tracking session: $sessionId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cancel demo tracking session", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get current demo bus location
     */
    suspend fun getCurrentBusLocation(busId: String): Result<BusLocation?> {
        return try {
            Log.d(TAG, "Getting current demo bus location for: $busId")
            val location = demoBusLocations[busId]
            Result.success(location)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get current demo bus location", e)
            Result.failure(e)
        }
    }
    
    /**
     * Calculate distance between two points using Haversine formula
     */
    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadius = 6371000.0 // Earth's radius in meters
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return earthRadius * c
    }
    
    /**
     * Calculate ETA based on distance and bus speed
     */
    fun calculateETA(distance: Double, speed: Double): Int {
        return if (speed > 0) {
            (distance / (speed * 1000 / 60)).toInt() // Convert to minutes
        } else {
            0
        }
    }
    
    /**
     * Format distance for display
     */
    fun formatDistance(distance: Double): String {
        return when {
            distance < 1000 -> "${distance.toInt()}m"
            else -> "${(distance / 1000).let { "%.1f".format(it) }}km"
        }
    }
    
    /**
     * Reset demo data for a specific bus
     */
    fun resetBusDemoData(busId: String) {
        val route = demoRoutes[busId]
        if (route != null) {
            demoBusLocations[busId] = route.first()
            currentRouteIndex[busId] = 0
            Log.d(TAG, "Reset demo data for bus: $busId")
        }
    }
    
    /**
     * Get all available demo bus IDs
     */
    fun getAvailableBusIds(): List<String> {
        return demoRoutes.keys.toList()
    }
}
