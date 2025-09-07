package com.example.userpanelnew.data

import android.util.Log
import com.example.userpanelnew.models.BusLocation
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

/**
 * Helper class to set up sample Firebase data for bus tracking
 * This is for development/testing purposes only
 */
class FirebaseDataSetup {
    
    private val db = FirebaseFirestore.getInstance()
    private val busesCollection = db.collection("buses")
    
    companion object {
        private const val TAG = "FirebaseDataSetup"
    }
    
    /**
     * Set up sample bus location data in Firebase
     * This simulates real-time bus locations for testing
     */
    suspend fun setupSampleBusData() {
        try {
            Log.d(TAG, "Setting up sample bus data...")
            
            val sampleBuses = listOf(
                "BUS101" to BusLocation(
                    busId = "BUS101",
                    latitude = 22.3072,
                    longitude = 73.1812,
                    speed = 35.0,
                    heading = 45.0,
                    accuracy = 10.0,
                    route = "Route 101",
                    isActive = true,
                    driverId = "DRIVER001",
                    capacity = 50,
                    currentPassengers = 25
                ),
                "BUS102" to BusLocation(
                    busId = "BUS102",
                    latitude = 22.3090,
                    longitude = 73.1850,
                    speed = 28.0,
                    heading = 90.0,
                    accuracy = 15.0,
                    route = "Route 102",
                    isActive = true,
                    driverId = "DRIVER002",
                    capacity = 45,
                    currentPassengers = 30
                ),
                "BUS103" to BusLocation(
                    busId = "BUS103",
                    latitude = 22.3050,
                    longitude = 73.1790,
                    speed = 42.0,
                    heading = 180.0,
                    accuracy = 8.0,
                    route = "Route 103",
                    isActive = true,
                    driverId = "DRIVER003",
                    capacity = 60,
                    currentPassengers = 40
                ),
                "BUS104" to BusLocation(
                    busId = "BUS104",
                    latitude = 22.3120,
                    longitude = 73.1880,
                    speed = 31.0,
                    heading = 270.0,
                    accuracy = 12.0,
                    route = "Route 104",
                    isActive = true,
                    driverId = "DRIVER004",
                    capacity = 55,
                    currentPassengers = 20
                )
            )
            
            // Save each bus location to Firebase
            sampleBuses.forEach { (busId, busLocation) ->
                busesCollection.document(busId)
                    .set(busLocation)
                    .await()
                Log.d(TAG, "Saved bus location for $busId")
            }
            
            Log.d(TAG, "Sample bus data setup completed successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up sample bus data", e)
            throw e
        }
    }
    
    /**
     * Simulate real-time bus movement
     * This function moves buses along predefined routes
     */
    suspend fun simulateBusMovement() {
        try {
            Log.d(TAG, "Starting bus movement simulation...")
            
            // Get all buses
            val snapshot = busesCollection.get().await()
            val buses = snapshot.documents.mapNotNull { document ->
                document.toObject(BusLocation::class.java)
            }
            
            buses.forEach { bus ->
                // Simulate movement by adding small random changes
                val newLatitude = bus.latitude + (Random.nextDouble() - 0.5) * 0.001
                val newLongitude = bus.longitude + (Random.nextDouble() - 0.5) * 0.001
                val newSpeed = maxOf(0.0, bus.speed + (Random.nextDouble() - 0.5) * 10)
                val newHeading = (bus.heading + (Random.nextDouble() - 0.5) * 30) % 360
                
                val updatedBus = bus.copy(
                    latitude = newLatitude,
                    longitude = newLongitude,
                    speed = newSpeed,
                    heading = newHeading,
                    timestamp = com.google.firebase.Timestamp.now()
                )
                
                busesCollection.document(bus.busId)
                    .set(updatedBus)
                    .await()
            }
            
            Log.d(TAG, "Bus movement simulation completed")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error simulating bus movement", e)
        }
    }
    
    /**
     * Clear all bus data (for testing)
     */
    suspend fun clearBusData() {
        try {
            Log.d(TAG, "Clearing all bus data...")
            
            val snapshot = busesCollection.get().await()
            val batch = db.batch()
            
            snapshot.documents.forEach { document ->
                batch.delete(document.reference)
            }
            
            batch.commit().await()
            Log.d(TAG, "All bus data cleared successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing bus data", e)
            throw e
        }
    }
    
    /**
     * Get current bus count
     */
    suspend fun getBusCount(): Int {
        return try {
            val snapshot = busesCollection.get().await()
            snapshot.size()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting bus count", e)
            0
        }
    }
}
