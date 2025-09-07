package com.example.userpanelnew.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Data class for real-time bus location tracking
 */
data class BusLocation(
    @DocumentId
    val busId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val speed: Double = 0.0, // in km/h
    val heading: Double = 0.0, // in degrees (0-360)
    val accuracy: Double = 0.0, // in meters
    @ServerTimestamp
    val timestamp: Timestamp? = null,
    val route: String = "",
    val isActive: Boolean = true,
    val driverId: String = "",
    val capacity: Int = 0,
    val currentPassengers: Int = 0
) {
    // Empty constructor for Firestore
    constructor() : this("", 0.0, 0.0, 0.0, 0.0, 0.0, null, "", true, "", 0, 0)
}

/**
 * Data class for tracking session
 */
data class TrackingSession(
    @DocumentId
    val sessionId: String = "",
    val userId: String = "",
    val busId: String = "",
    val startTime: Timestamp? = null,
    val endTime: Timestamp? = null,
    val status: TrackingStatus = TrackingStatus.ACTIVE,
    val userLocation: UserLocation? = null,
    val busLocation: BusLocation? = null,
    val distance: Double = 0.0, // in meters
    val estimatedArrival: Timestamp? = null,
    @ServerTimestamp
    val createdAt: Timestamp? = null
) {
    // Empty constructor for Firestore
    constructor() : this("", "", "", null, null, TrackingStatus.ACTIVE, null, null, 0.0, null, null)
}

/**
 * Data class for user location during tracking
 */
data class UserLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val accuracy: Double = 0.0, // in meters
    @ServerTimestamp
    val timestamp: Timestamp? = null
) {
    // Empty constructor for Firestore
    constructor() : this(0.0, 0.0, 0.0, null)
}

/**
 * Enum for tracking session status
 */
enum class TrackingStatus {
    ACTIVE,
    PAUSED,
    COMPLETED,
    CANCELLED
}

/**
 * Data class for tracking notification state
 */
data class TrackingNotificationState(
    val isVisible: Boolean = false,
    val busId: String = "",
    val busRoute: String = "",
    val eta: Int = 0, // in minutes
    val distance: Double = 0.0, // in meters
    val isLive: Boolean = false
)

