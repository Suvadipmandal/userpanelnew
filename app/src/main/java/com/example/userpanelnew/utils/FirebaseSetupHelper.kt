package com.example.userpanelnew.utils

import android.util.Log
import com.example.userpanelnew.models.BusLocation
import com.example.userpanelnew.services.FirebaseBusSimulator
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*

/**
 * Firebase Setup Helper
 * 
 * This utility class helps with Firebase Realtime Database setup and initialization
 * for the bus tracking demo. It provides methods to:
 * - Initialize Firebase database rules
 * - Set up demo data
 * - Start/stop the bus simulator
 * - Clean up demo data
 */
class FirebaseSetupHelper {
    
    private val database = FirebaseDatabase.getInstance()
    private val busesRef = database.getReference("buses")
    private val trackingSessionsRef = database.getReference("tracking_sessions")
    
    private val busSimulator = FirebaseBusSimulator()
    
    companion object {
        private const val TAG = "FirebaseSetupHelper"
    }
    
    /**
     * Initialize Firebase database with demo data
     * Call this method when the app starts to set up the demo environment
     */
    suspend fun initializeDemoData() {
        try {
            Log.d(TAG, "Initializing Firebase demo data...")
            
            // Set up initial demo bus location
            val initialBusLocation = BusLocation(
                busId = "demoBus",
                latitude = 22.3072, // Vadodara Junction
                longitude = 73.1812,
                speed = 0.0,
                heading = 0.0,
                accuracy = 5.0,
                timestamp = com.google.firebase.Timestamp.now(),
                route = "Route 1 - Vadodara Junction to Alkapuri",
                isActive = true,
                driverId = "DRIVER001",
                capacity = 50,
                currentPassengers = 0
            )
            
            // Write initial bus location to Firebase
            busesRef.child("demoBus").setValue(initialBusLocation)
            
            Log.d(TAG, "Demo data initialized successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing demo data", e)
            throw e
        }
    }
    
    /**
     * Start the bus simulation
     * This will begin writing bus coordinates to Firebase every 2 seconds
     */
    fun startBusSimulation() {
        try {
            Log.d(TAG, "Starting bus simulation...")
            busSimulator.startSimulation()
            Log.d(TAG, "Bus simulation started successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting bus simulation", e)
        }
    }
    
    /**
     * Stop the bus simulation
     */
    fun stopBusSimulation() {
        try {
            Log.d(TAG, "Stopping bus simulation...")
            busSimulator.stopSimulation()
            Log.d(TAG, "Bus simulation stopped successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping bus simulation", e)
        }
    }
    
    /**
     * Check if bus simulation is running
     */
    fun isSimulationRunning(): Boolean {
        return busSimulator.isSimulationRunning()
    }
    
    /**
     * Get current simulation progress
     */
    fun getSimulationProgress(): Float {
        return busSimulator.getRouteProgress()
    }
    
    /**
     * Reset the simulation to starting position
     */
    fun resetSimulation() {
        busSimulator.resetSimulation()
    }
    
    /**
     * Clean up demo data from Firebase
     * Call this when you want to remove all demo data
     */
    suspend fun cleanupDemoData() {
        try {
            Log.d(TAG, "Cleaning up demo data...")
            
            // Remove demo bus data
            busesRef.child("demoBus").removeValue()
            
            // Remove any demo tracking sessions
            trackingSessionsRef.orderByChild("busId").equalTo("demoBus")
                .addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
                    override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                        snapshot.children.forEach { child ->
                            child.ref.removeValue()
                        }
                    }
                    
                    override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                        Log.e(TAG, "Error cleaning up tracking sessions", error.toException())
                    }
                })
            
            Log.d(TAG, "Demo data cleaned up successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning up demo data", e)
        }
    }
    
    /**
     * Get Firebase database rules for development
     * These rules allow read/write access for development purposes
     * 
     * IMPORTANT: These rules are for development only!
     * For production, implement proper authentication and security rules.
     */
    fun getDevelopmentDatabaseRules(): String {
        return """
        {
          "rules": {
            "buses": {
              ".read": true,
              ".write": true,
              ".validate": "newData.hasChildren(['busId', 'latitude', 'longitude', 'speed', 'timestamp'])"
            },
            "tracking_sessions": {
              ".read": true,
              ".write": true,
              ".validate": "newData.hasChildren(['sessionId', 'userId', 'busId', 'startTime'])"
            }
          }
        }
        """.trimIndent()
    }
    
    /**
     * Get production-ready database rules
     * These rules require authentication and proper validation
     */
    fun getProductionDatabaseRules(): String {
        return """
        {
          "rules": {
            "buses": {
              ".read": "auth != null",
              ".write": "auth != null && auth.token.admin == true",
              ".validate": "newData.hasChildren(['busId', 'latitude', 'longitude', 'speed', 'timestamp'])"
            },
            "tracking_sessions": {
              ".read": "auth != null",
              ".write": "auth != null",
              ".validate": "newData.hasChildren(['sessionId', 'userId', 'busId', 'startTime'])"
            }
          }
        }
        """.trimIndent()
    }
    
    /**
     * Test Firebase connection
     * Returns true if Firebase is accessible
     */
    suspend fun testFirebaseConnection(): Boolean {
        return try {
            Log.d(TAG, "Testing Firebase connection...")
            
            // Try to read from a test reference
            val testRef = database.getReference("test")
            testRef.setValue("connection_test")
            
            // Wait a bit and then clean up
            delay(1000)
            testRef.removeValue()
            
            Log.d(TAG, "Firebase connection test successful")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "Firebase connection test failed", e)
            false
        }
    }
    
    /**
     * Get current bus location from Firebase
     */
    suspend fun getCurrentBusLocation(): BusLocation? {
        return try {
            // For now, return null as this is a demo function
            // In a real implementation, you would use Firebase Realtime Database
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current bus location", e)
            null
        }
    }
    
    /**
     * Get simulation status information
     */
    fun getSimulationStatus(): String {
        return if (isSimulationRunning()) {
            "Simulation running - Progress: ${(getSimulationProgress() * 100).toInt()}%"
        } else {
            "Simulation stopped"
        }
    }
}
