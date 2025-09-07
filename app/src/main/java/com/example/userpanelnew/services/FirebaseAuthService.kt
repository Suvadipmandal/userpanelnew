package com.example.userpanelnew.services

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class FirebaseAuthService {
    
    private val auth = FirebaseAuth.getInstance()
    
    suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        name: String
    ): Result<FirebaseUser> {
        return try {
            Log.d("FirebaseAuth", "Starting sign up for email: $email")
            
            // Create user with email and password
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                // Update user profile with display name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                
                user.updateProfile(profileUpdates).await()
                
                // Send email verification
                user.sendEmailVerification().await()
                
                Log.d("FirebaseAuth", "User created successfully: ${user.uid}")
                Result.success(user)
            } else {
                Log.e("FirebaseAuth", "User creation failed: user is null")
                Result.failure(Exception("User creation failed"))
            }
            
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Sign up failed: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            Log.d("FirebaseAuth", "Starting sign in for email: $email")
            
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                Log.d("FirebaseAuth", "Sign in successful: ${user.uid}")
                Result.success(user)
            } else {
                Log.e("FirebaseAuth", "Sign in failed: user is null")
                Result.failure(Exception("Sign in failed"))
            }
            
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Sign in failed: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Log.d("FirebaseAuth", "Sign out successful")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Sign out failed: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    
    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }
    
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Log.d("FirebaseAuth", "Password reset email sent to: $email")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Password reset failed: ${e.message}", e)
            Result.failure(e)
        }
    }
}

