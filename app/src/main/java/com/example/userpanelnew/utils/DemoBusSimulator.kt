package com.example.userpanelnew.utils

import android.util.Log
import com.mapbox.geojson.Point
import kotlin.math.*

/**
 * Demo Bus Simulator for Vadodara
 * Simulates realistic bus movement along predefined routes in Vadodara
 */
class DemoBusSimulator {
    
    companion object {
        private const val TAG = "DemoBusSimulator"
        
        // Vadodara city center coordinates
        private const val VADODARA_CENTER_LAT = 22.3072
        private const val VADODARA_CENTER_LNG = 73.1812
        
        // Route simulation parameters
        private const val ROUTE_SPEED_KMH = 35.0 // Average bus speed in km/h
        private const val UPDATE_INTERVAL_MS = 2000L // Update every 2 seconds
        private const val METERS_PER_UPDATE = 20.0 // Approximate meters moved per update
    }
    
    // Demo routes in Vadodara (realistic coordinates)
    private val demoRoutes = mapOf(
        "ROUTE_101" to listOf(
            // Route 101: City Center to Railway Station
            Point.fromLngLat(73.1812, 22.3072), // City Center
            Point.fromLngLat(73.1820, 22.3080),
            Point.fromLngLat(73.1830, 22.3090),
            Point.fromLngLat(73.1840, 22.3100),
            Point.fromLngLat(73.1850, 22.3110),
            Point.fromLngLat(73.1860, 22.3120),
            Point.fromLngLat(73.1870, 22.3130),
            Point.fromLngLat(73.1880, 22.3140),
            Point.fromLngLat(73.1890, 22.3150),
            Point.fromLngLat(73.1900, 22.3160), // Railway Station area
            Point.fromLngLat(73.1910, 22.3170),
            Point.fromLngLat(73.1920, 22.3180),
            Point.fromLngLat(73.1930, 22.3190),
            Point.fromLngLat(73.1940, 22.3200),
            Point.fromLngLat(73.1950, 22.3210)
        ),
        "ROUTE_102" to listOf(
            // Route 102: City Center to Airport
            Point.fromLngLat(73.1812, 22.3072), // City Center
            Point.fromLngLat(73.1800, 22.3080),
            Point.fromLngLat(73.1790, 22.3090),
            Point.fromLngLat(73.1780, 22.3100),
            Point.fromLngLat(73.1770, 22.3110),
            Point.fromLngLat(73.1760, 22.3120),
            Point.fromLngLat(73.1750, 22.3130),
            Point.fromLngLat(73.1740, 22.3140),
            Point.fromLngLat(73.1730, 22.3150),
            Point.fromLngLat(73.1720, 22.3160),
            Point.fromLngLat(73.1710, 22.3170),
            Point.fromLngLat(73.1700, 22.3180),
            Point.fromLngLat(73.1690, 22.3190),
            Point.fromLngLat(73.1680, 22.3200),
            Point.fromLngLat(73.1670, 22.3210) // Airport area
        ),
        "ROUTE_103" to listOf(
            // Route 103: City Center to University
            Point.fromLngLat(73.1812, 22.3072), // City Center
            Point.fromLngLat(73.1825, 22.3060),
            Point.fromLngLat(73.1840, 22.3050),
            Point.fromLngLat(73.1855, 22.3040),
            Point.fromLngLat(73.1870, 22.3030),
            Point.fromLngLat(73.1885, 22.3020),
            Point.fromLngLat(73.1900, 22.3010),
            Point.fromLngLat(73.1915, 22.3000),
            Point.fromLngLat(73.1930, 22.2990),
            Point.fromLngLat(73.1945, 22.2980),
            Point.fromLngLat(73.1960, 22.2970),
            Point.fromLngLat(73.1975, 22.2960),
            Point.fromLngLat(73.1990, 22.2950),
            Point.fromLngLat(73.2005, 22.2940),
            Point.fromLngLat(73.2020, 22.2930) // University area
        )
    )
    
    private var currentRouteIndex = 0
    private var currentRoute = "ROUTE_101"
    private var isSimulationRunning = false
    
    /**
     * Start demo bus simulation
     */
    fun startSimulation(routeId: String = "ROUTE_101"): Boolean {
        if (isSimulationRunning) {
            Log.w(TAG, "Simulation already running")
            return false
        }
        
        currentRoute = routeId
        currentRouteIndex = 0
        isSimulationRunning = true
        
        Log.d(TAG, "Started demo bus simulation on route: $routeId")
        return true
    }
    
    /**
     * Stop demo bus simulation
     */
    fun stopSimulation() {
        isSimulationRunning = false
        Log.d(TAG, "Stopped demo bus simulation")
    }
    
    /**
     * Get next bus location in the simulation
     */
    fun getNextLocation(): Point? {
        if (!isSimulationRunning) {
            return null
        }
        
        val route = demoRoutes[currentRoute] ?: return null
        
        // Move to next location in route
        currentRouteIndex = (currentRouteIndex + 1) % route.size
        val nextPoint = route[currentRouteIndex]
        
        Log.d(TAG, "Next location: ${nextPoint.latitude()}, ${nextPoint.longitude()}")
        return nextPoint
    }
    
    /**
     * Get current bus location
     */
    fun getCurrentLocation(): Point? {
        if (!isSimulationRunning) {
            return null
        }
        
        val route = demoRoutes[currentRoute] ?: return null
        return route[currentRouteIndex]
    }
    
    /**
     * Get bus speed based on current location and route
     */
    fun getCurrentSpeed(): Double {
        // Simulate realistic speed variations
        val baseSpeed = ROUTE_SPEED_KMH
        val variation = (Math.random() - 0.5) * 10.0 // ±5 km/h variation
        return maxOf(15.0, minOf(50.0, baseSpeed + variation))
    }
    
    /**
     * Get bus heading based on current and next location
     */
    fun getCurrentHeading(): Double {
        val route = demoRoutes[currentRoute] ?: return 0.0
        val currentIndex = currentRouteIndex
        val nextIndex = (currentIndex + 1) % route.size
        
        val currentPoint = route[currentIndex]
        val nextPoint = route[nextIndex]
        
        return calculateBearing(
            currentPoint.latitude(), currentPoint.longitude(),
            nextPoint.latitude(), nextPoint.longitude()
        )
    }
    
    /**
     * Get available routes
     */
    fun getAvailableRoutes(): List<String> {
        return demoRoutes.keys.toList()
    }
    
    /**
     * Switch to a different route
     */
    fun switchRoute(routeId: String): Boolean {
        if (demoRoutes.containsKey(routeId)) {
            currentRoute = routeId
            currentRouteIndex = 0
            Log.d(TAG, "Switched to route: $routeId")
            return true
        }
        return false
    }
    
    /**
     * Check if simulation is running
     */
    fun isRunning(): Boolean {
        return isSimulationRunning
    }
    
    /**
     * Get current route progress (0.0 to 1.0)
     */
    fun getRouteProgress(): Double {
        val route = demoRoutes[currentRoute] ?: return 0.0
        return currentRouteIndex.toDouble() / route.size
    }
    
    /**
     * Calculate bearing between two points
     */
    private fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLon = Math.toRadians(lon2 - lon1)
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)
        
        val y = sin(dLon) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(dLon)
        
        var bearing = Math.toDegrees(atan2(y, x))
        bearing = (bearing + 360) % 360
        
        return bearing
    }
    
    /**
     * Calculate distance between two points
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
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
     * Get route information
     */
    fun getRouteInfo(routeId: String): String {
        return when (routeId) {
            "ROUTE_101" -> "City Center → Railway Station"
            "ROUTE_102" -> "City Center → Airport"
            "ROUTE_103" -> "City Center → University"
            else -> "Unknown Route"
        }
    }
    
    /**
     * Get estimated time to complete route (in minutes)
     */
    fun getEstimatedRouteTime(routeId: String): Int {
        val route = demoRoutes[routeId] ?: return 0
        
        var totalDistance = 0.0
        for (i in 0 until route.size - 1) {
            val point1 = route[i]
            val point2 = route[i + 1]
            totalDistance += calculateDistance(
                point1.latitude(), point1.longitude(),
                point2.latitude(), point2.longitude()
            )
        }
        
        // Convert to minutes (distance in meters, speed in km/h)
        val timeInHours = totalDistance / (ROUTE_SPEED_KMH * 1000)
        return (timeInHours * 60).toInt()
    }
}
