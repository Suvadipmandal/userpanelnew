package com.example.userpanelnew.services

import android.util.Log
import com.example.userpanelnew.models.Ride
import com.example.userpanelnew.models.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirestoreService {
    
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    
    suspend fun saveUserProfile(userProfile: UserProfile): Result<UserProfile> {
        return try {
            Log.d("FirestoreService", "Saving user profile for: ${userProfile.email}")
            
            // Save user profile to Firestore
            usersCollection.document(userProfile.uid)
                .set(userProfile, SetOptions.merge())
                .await()
            
            Log.d("FirestoreService", "User profile saved successfully: ${userProfile.uid}")
            Result.success(userProfile)
            
        } catch (e: Exception) {
            Log.e("FirestoreService", "Failed to save user profile: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun getUserProfile(uid: String): Result<UserProfile?> {
        return try {
            Log.d("FirestoreService", "Getting user profile for: $uid")
            
            val document = usersCollection.document(uid).get().await()
            
            if (document.exists()) {
                val userProfile = document.toObject(UserProfile::class.java)
                Log.d("FirestoreService", "User profile retrieved successfully")
                Result.success(userProfile)
            } else {
                Log.d("FirestoreService", "User profile not found")
                Result.success(null)
            }
            
        } catch (e: Exception) {
            Log.e("FirestoreService", "Failed to get user profile: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateUserProfile(uid: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            Log.d("FirestoreService", "Updating user profile for: $uid")
            
            usersCollection.document(uid)
                .update(updates)
                .await()
            
            Log.d("FirestoreService", "User profile updated successfully")
            Result.success(Unit)
            
        } catch (e: Exception) {
            Log.e("FirestoreService", "Failed to update user profile: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Rides subcollection methods
    suspend fun saveRide(userId: String, ride: Ride): Result<Ride> {
        return try {
            Log.d("FirestoreService", "Saving ride for user: $userId")
            
            val rideRef = usersCollection.document(userId)
                .collection("rides")
                .document()
            
            val rideWithId = ride.copy(rideId = rideRef.id, userId = userId)
            
            rideRef.set(rideWithId).await()
            
            Log.d("FirestoreService", "Ride saved successfully: ${rideWithId.rideId}")
            Result.success(rideWithId)
            
        } catch (e: Exception) {
            Log.e("FirestoreService", "Failed to save ride: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun getUserRides(userId: String): Result<List<Ride>> {
        return try {
            Log.d("FirestoreService", "Getting rides for user: $userId")
            
            val snapshot = usersCollection.document(userId)
                .collection("rides")
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            
            val rides = snapshot.documents.mapNotNull { document ->
                document.toObject(Ride::class.java)
            }
            
            Log.d("FirestoreService", "Retrieved ${rides.size} rides")
            Result.success(rides)
            
        } catch (e: Exception) {
            Log.e("FirestoreService", "Failed to get rides: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateRideStatus(userId: String, rideId: String, status: String): Result<Unit> {
        return try {
            Log.d("FirestoreService", "Updating ride status: $rideId to $status")
            
            usersCollection.document(userId)
                .collection("rides")
                .document(rideId)
                .update("status", status)
                .await()
            
            Log.d("FirestoreService", "Ride status updated successfully")
            Result.success(Unit)
            
        } catch (e: Exception) {
            Log.e("FirestoreService", "Failed to update ride status: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun deleteRide(userId: String, rideId: String): Result<Unit> {
        return try {
            Log.d("FirestoreService", "Deleting ride: $rideId")
            
            usersCollection.document(userId)
                .collection("rides")
                .document(rideId)
                .delete()
                .await()
            
            Log.d("FirestoreService", "Ride deleted successfully")
            Result.success(Unit)
            
        } catch (e: Exception) {
            Log.e("FirestoreService", "Failed to delete ride: ${e.message}", e)
            Result.failure(e)
        }
    }
}

