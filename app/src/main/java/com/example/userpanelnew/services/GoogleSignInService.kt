package com.example.userpanelnew.services

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.tasks.await

class GoogleSignInService(private val context: Context) {
    
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("23991952685-b3qeorbcev63j70sol79r1j5vev6jdm0.apps.googleusercontent.com")
            .requestEmail()
            .build()
        
        GoogleSignIn.getClient(context, gso)
    }
    
    fun handleSignInResult(data: android.content.Intent?): Result<String> {
        return try {
            Log.d("GoogleSignIn", "Handling Google Sign-In result")
            
            if (data == null) {
                Log.e("GoogleSignIn", "Sign-in result data is null")
                return Result.failure(Exception("Sign-in result data is null"))
            }
            
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            
            val idToken = account.idToken
            if (idToken != null) {
                Log.d("GoogleSignIn", "Google Sign-In successful, got ID token")
                Result.success(idToken)
            } else {
                Log.e("GoogleSignIn", "Google Sign-In failed: No ID token received")
                Result.failure(Exception("No ID token received"))
            }
            
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Google Sign-In API failed: ${e.message}", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("GoogleSignIn", "Google Sign-In failed: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    fun getSignInIntent() = googleSignInClient.signInIntent
    
    suspend fun signOut() {
        try {
            googleSignInClient.signOut().await()
            Log.d("GoogleSignIn", "Google Sign-Out successful")
        } catch (e: Exception) {
            Log.e("GoogleSignIn", "Google Sign-Out failed: ${e.message}", e)
        }
    }
}
