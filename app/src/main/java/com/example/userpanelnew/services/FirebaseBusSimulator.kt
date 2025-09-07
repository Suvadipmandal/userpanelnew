package com.example.userpanelnew.services

import android.util.Log
import com.example.userpanelnew.models.BusLocation
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*
import kotlin.math.*

/**
 * Firebase Bus Simulator Service
 * 
 * This service simulates a bus moving along a predefined route in Vadodara
 * and writes the coordinates to Firebase Realtime Database every 2 seconds.
 * 
 * The bus follows a realistic route around Vadodara Junction and nearby areas.
 */
class FirebaseBusSimulator {
    
    private val database = FirebaseDatabase.getInstance()
    private val busesRef = database.getReference("buses")
    
    private var simulationJob: Job? = null
    private var isSimulating = false
    
    companion object {
        private const val TAG = "FirebaseBusSimulator"
        private const val UPDATE_INTERVAL = 2000L // 2 seconds as requested
        private const val DEMO_BUS_ID = "demoBus"
    }
    
    // Predefined route around Vadodara Junction (22.3072, 73.1812)
    // This creates a realistic bus route with multiple stops
    private val vadodaraRoute = listOf(
        // Starting from Vadodara Junction
        BusLocation(DEMO_BUS_ID, 22.3072, 73.1812, 0.0, 0.0, 5.0, null, "Route 1", true, "DRIVER001", 50, 0),
        
        // Moving towards Sayajigunj
        BusLocation(DEMO_BUS_ID, 22.3080, 73.1820, 25.0, 45.0, 8.0, null, "Route 1", true, "DRIVER001", 50, 5),
        BusLocation(DEMO_BUS_ID, 22.3090, 73.1830, 30.0, 50.0, 10.0, null, "Route 1", true, "DRIVER001", 50, 8),
        BusLocation(DEMO_BUS_ID, 22.3100, 73.1840, 35.0, 55.0, 12.0, null, "Route 1", true, "DRIVER001", 50, 12),
        
        // Towards Alkapuri
        BusLocation(DEMO_BUS_ID, 22.3110, 73.1850, 40.0, 60.0, 9.0, null, "Route 1", true, "DRIVER001", 50, 15),
        BusLocation(DEMO_BUS_ID, 22.3120, 73.1860, 38.0, 65.0, 11.0, null, "Route 1", true, "DRIVER001", 50, 18),
        BusLocation(DEMO_BUS_ID, 22.3130, 73.1870, 42.0, 70.0, 7.0, null, "Route 1", true, "DRIVER001", 50, 22),
        
        // Towards Race Course
        BusLocation(DEMO_BUS_ID, 22.3140, 73.1880, 45.0, 75.0, 13.0, null, "Route 1", true, "DRIVER001", 50, 25),
        BusLocation(DEMO_BUS_ID, 22.3150, 73.1890, 48.0, 80.0, 6.0, null, "Route 1", true, "DRIVER001", 50, 28),
        BusLocation(DEMO_BUS_ID, 22.3160, 73.1900, 50.0, 85.0, 9.0, null, "Route 1", true, "DRIVER001", 50, 32),
        
        // Turning back towards city center
        BusLocation(DEMO_BUS_ID, 22.3150, 73.1910, 35.0, 90.0, 14.0, null, "Route 1", true, "DRIVER001", 50, 35),
        BusLocation(DEMO_BUS_ID, 22.3140, 73.1920, 32.0, 95.0, 10.0, null, "Route 1", true, "DRIVER001", 50, 38),
        BusLocation(DEMO_BUS_ID, 22.3130, 73.1930, 28.0, 100.0, 12.0, null, "Route 1", true, "DRIVER001", 50, 40),
        
        // Towards Railway Station
        BusLocation(DEMO_BUS_ID, 22.3120, 73.1940, 25.0, 105.0, 8.0, null, "Route 1", true, "DRIVER001", 50, 42),
        BusLocation(DEMO_BUS_ID, 22.3110, 73.1950, 22.0, 110.0, 11.0, null, "Route 1", true, "DRIVER001", 50, 45),
        BusLocation(DEMO_BUS_ID, 22.3100, 73.1960, 20.0, 115.0, 9.0, null, "Route 1", true, "DRIVER001", 50, 48),
        
        // Back towards Vadodara Junction
        BusLocation(DEMO_BUS_ID, 22.3090, 73.1950, 18.0, 120.0, 13.0, null, "Route 1", true, "DRIVER001", 50, 45),
        BusLocation(DEMO_BUS_ID, 22.3080, 73.1940, 15.0, 125.0, 7.0, null, "Route 1", true, "DRIVER001", 50, 42),
        BusLocation(DEMO_BUS_ID, 22.3070, 73.1930, 12.0, 130.0, 10.0, null, "Route 1", true, "DRIVER001", 50, 38),
        BusLocation(DEMO_BUS_ID, 22.3060, 73.1920, 10.0, 135.0, 8.0, null, "Route 1", true, "DRIVER001", 50, 35),
        BusLocation(DEMO_BUS_ID, 22.3050, 73.1910, 8.0, 140.0, 12.0, null, "Route 1", true, "DRIVER001", 50, 32),
        
        // Final approach to Vadodara Junction
        BusLocation(DEMO_BUS_ID, 22.3060, 73.1900, 5.0, 145.0, 6.0, null, "Route 1", true, "DRIVER001", 50, 28),
        BusLocation(DEMO_BUS_ID, 22.3070, 73.1890, 3.0, 150.0, 9.0, null, "Route 1", true, "DRIVER001", 50, 25),
        BusLocation(DEMO_BUS_ID, 22.3072, 73.1880, 0.0, 155.0, 5.0, null, "Route 1", true, "DRIVER001", 50, 22),
        
        // Back to starting point (Vadodara Junction)
        BusLocation(DEMO_BUS_ID, 22.3072, 73.1812, 0.0, 0.0, 5.0, null, "Route 1", true, "DRIVER001", 50, 0)
    )
    
    private var currentRouteIndex = 0
    
    /**
     * Start the bus simulation
     * This will write bus coordinates to Firebase every 2 seconds
     */
    fun startSimulation() {
        if (isSimulating) {
            Log.w(TAG, "Simulation is already running")
            return
        }
        
        Log.d(TAG, "Starting Firebase bus simulation")
        isSimulating = true
        
        simulationJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                while (isSimulating) {
                    // Get current bus location from route
                    val currentLocation = getCurrentBusLocation()
                    
                    // Write to Firebase Realtime Database
                    writeBusLocationToFirebase(currentLocation)
                    
                    // Move to next location in route
                    moveToNextLocation()
                    
                    // Wait for 2 seconds before next update
                    delay(UPDATE_INTERVAL)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in bus simulation", e)
            }
        }
    }
    
    /**
     * Stop the bus simulation
     */
    fun stopSimulation() {
        if (!isSimulating) {
            Log.w(TAG, "Simulation is not running")
            return
        }
        
        Log.d(TAG, "Stopping Firebase bus simulation")
        isSimulating = false
        simulationJob?.cancel()
        simulationJob = null
    }
    
    /**
     * Get current bus location from the route
     */
    private fun getCurrentBusLocation(): BusLocation {
        val baseLocation = vadodaraRoute[currentRouteIndex]
        
        // Add some realistic variation to make it more dynamic
        val latVariation = (Math.random() - 0.5) * 0.0001 // Small random variation
        val lngVariation = (Math.random() - 0.5) * 0.0001
        val speedVariation = (Math.random() - 0.5) * 5.0 // Speed variation
        
        return baseLocation.copy(
            latitude = baseLocation.latitude + latVariation,
            longitude = baseLocation.longitude + lngVariation,
            speed = maxOf(0.0, baseLocation.speed + speedVariation),
            timestamp = com.google.firebase.Timestamp.now(),
            currentPassengers = (20..45).random() // Random passenger count
        )
    }
    
    /**
     * Move to next location in the route
     */
    private fun moveToNextLocation() {
        currentRouteIndex = (currentRouteIndex + 1) % vadodaraRoute.size
    }
    
    /**
     * Write bus location to Firebase Realtime Database
     */
    private suspend fun writeBusLocationToFirebase(busLocation: BusLocation) {
        try {
            // Write to /buses/demoBus path as requested
            busesRef.child(DEMO_BUS_ID).setValue(busLocation)
            
            Log.d(TAG, "Updated bus location in Firebase: " +
                    "Lat: ${busLocation.latitude}, Lng: ${busLocation.longitude}, " +
                    "Speed: ${busLocation.speed} km/h, Passengers: ${busLocation.currentPassengers}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error writing bus location to Firebase", e)
        }
    }
    
    /**
     * Reset the simulation to start from the beginning
     */
    fun resetSimulation() {
        currentRouteIndex = 0
        Log.d(TAG, "Simulation reset to starting position")
    }
    
    /**
     * Get current simulation status
     */
    fun isSimulationRunning(): Boolean {
        return isSimulating
    }
    
    /**
     * Get current route progress (0.0 to 1.0)
     */
    fun getRouteProgress(): Float {
        return currentRouteIndex.toFloat() / vadodaraRoute.size
    }
    
    /**
     * Get current bus location without Firebase update
     */
    fun getCurrentLocation(): BusLocation {
        return getCurrentBusLocation()
    }
    
    /**
     * Set custom route index (for testing)
     */
    fun setRouteIndex(index: Int) {
        if (index in 0 until vadodaraRoute.size) {
            currentRouteIndex = index
            Log.d(TAG, "Route index set to: $index")
        }
    }
    
    /**
     * Get route information
     */
    fun getRouteInfo(): String {
        return "Vadodara Bus Route - ${vadodaraRoute.size} stops, " +
                "Current: ${currentRouteIndex + 1}/${vadodaraRoute.size}"
    }
}
