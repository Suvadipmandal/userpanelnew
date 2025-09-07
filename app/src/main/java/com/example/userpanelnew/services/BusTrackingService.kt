package com.example.userpanelnew.services

import android.util.Log
import com.example.userpanelnew.models.*
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.math.*

class BusTrackingService {
    
    private val database = FirebaseDatabase.getInstance()
    private val busesRef = database.getReference("buses")
    private val trackingSessionsRef = database.getReference("tracking_sessions")
    
    companion object {
        private const val TAG = "BusTrackingService"
    }
    
    /**
     * Get real-time bus location updates from Firebase Realtime Database
     */
    fun getBusLocationUpdates(busId: String): Flow<BusLocation?> = callbackFlow {
        Log.d(TAG, "Starting bus location updates for: $busId")
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val busLocation = snapshot.getValue(BusLocation::class.java)
                    Log.d(TAG, "Received bus location update: $busLocation")
                    trySend(busLocation)
                } else {
                    Log.d(TAG, "Bus location does not exist")
                    trySend(null)
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error listening to bus location updates", error.toException())
                close(error.toException())
            }
        }
        
        busesRef.child(busId).addValueEventListener(listener)
        
        awaitClose {
            Log.d(TAG, "Stopping bus location updates for: $busId")
            busesRef.child(busId).removeEventListener(listener)
        }
    }
    
    /**
     * Create a new tracking session
     */
    suspend fun createTrackingSession(
        userId: String,
        busId: String,
        userLocation: UserLocation
    ): Result<TrackingSession> {
        return try {
            Log.d(TAG, "Creating tracking session for user: $userId, bus: $busId")
            
            val sessionId = trackingSessionsRef.push().key ?: throw Exception("Failed to generate session ID")
            val session = TrackingSession(
                sessionId = sessionId,
                userId = userId,
                busId = busId,
                startTime = com.google.firebase.Timestamp.now(),
                status = TrackingStatus.ACTIVE,
                userLocation = userLocation
            )
            
            trackingSessionsRef.child(sessionId).setValue(session).await()
            
            Log.d(TAG, "Tracking session created successfully: ${session.sessionId}")
            Result.success(session)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create tracking session", e)
            Result.failure(e)
        }
    }
    
    /**
     * Update tracking session with current locations
     */
    suspend fun updateTrackingSession(
        sessionId: String,
        userLocation: UserLocation,
        busLocation: BusLocation
    ): Result<Unit> {
        return try {
            Log.d(TAG, "Updating tracking session: $sessionId")
            
            val distance = calculateDistance(
                userLocation.latitude, userLocation.longitude,
                busLocation.latitude, busLocation.longitude
            )
            
            val updates = mapOf(
                "userLocation" to userLocation,
                "busLocation" to busLocation,
                "distance" to distance
            )
            
            trackingSessionsRef.child(sessionId).updateChildren(updates).await()
            
            Log.d(TAG, "Tracking session updated successfully")
            Result.success(Unit)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update tracking session", e)
            Result.failure(e)
        }
    }
    
    /**
     * End tracking session
     */
    suspend fun endTrackingSession(sessionId: String): Result<Unit> {
        return try {
            Log.d(TAG, "Ending tracking session: $sessionId")
            
            val updates = mapOf(
                "endTime" to com.google.firebase.Timestamp.now(),
                "status" to TrackingStatus.COMPLETED
            )
            
            trackingSessionsRef.child(sessionId).updateChildren(updates).await()
            
            Log.d(TAG, "Tracking session ended successfully")
            Result.success(Unit)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to end tracking session", e)
            Result.failure(e)
        }
    }
    
    /**
     * Cancel tracking session
     */
    suspend fun cancelTrackingSession(sessionId: String): Result<Unit> {
        return try {
            Log.d(TAG, "Cancelling tracking session: $sessionId")
            
            val updates = mapOf(
                "endTime" to com.google.firebase.Timestamp.now(),
                "status" to TrackingStatus.CANCELLED
            )
            
            trackingSessionsRef.child(sessionId).updateChildren(updates).await()
            
            Log.d(TAG, "Tracking session cancelled successfully")
            Result.success(Unit)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cancel tracking session", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get current bus location (one-time fetch)
     */
    suspend fun getCurrentBusLocation(busId: String): Result<BusLocation?> {
        return try {
            Log.d(TAG, "Getting current bus location for: $busId")
            
            val snapshot = busesRef.child(busId).get().await()
            
            if (snapshot.exists()) {
                val busLocation = snapshot.getValue(BusLocation::class.java)
                Log.d(TAG, "Retrieved bus location: $busLocation")
                Result.success(busLocation)
            } else {
                Log.d(TAG, "Bus location does not exist")
                Result.success(null)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get current bus location", e)
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
}

