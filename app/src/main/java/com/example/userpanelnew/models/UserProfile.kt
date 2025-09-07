package com.example.userpanelnew.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class UserProfile(
    @DocumentId
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    val phone: String = "",
    val profileImageUrl: String = "",
    val isActive: Boolean = true
) {
    // Empty constructor for Firestore
    constructor() : this("", "", "", null, "", "", true)
}

// Data class for rides subcollection
data class Ride(
    @DocumentId
    val rideId: String = "",
    val userId: String = "",
    val busNumber: String = "",
    val fromLocation: String = "",
    val toLocation: String = "",
    val startTime: Timestamp? = null,
    val endTime: Timestamp? = null,
    val fare: Double = 0.0,
    val status: String = "active", // active, completed, cancelled
    @ServerTimestamp
    val createdAt: Timestamp? = null
) {
    // Empty constructor for Firestore
    constructor() : this("", "", "", "", "", null, null, 0.0, "active", null)
}

