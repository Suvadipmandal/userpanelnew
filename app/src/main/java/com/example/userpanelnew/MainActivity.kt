package com.example.userpanelnew

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.userpanelnew.ui.MainApp
import com.example.userpanelnew.ui.theme.UserPanelNewTheme
import com.example.userpanelnew.utils.NavigationEventBus
import com.example.userpanelnew.utils.NavigationEvent
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set up global exception handler for debugging
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            android.util.Log.e("MainActivity", "Uncaught exception in thread ${thread.name}: ${throwable.message}", throwable)
            throwable.printStackTrace()
            
            // Don't crash the app for non-fatal exceptions
            if (throwable is OutOfMemoryError || throwable is SecurityException) {
                // These are fatal exceptions that should crash the app
                throw throwable
            }
        }
        
        // --- START: FIREBASE CONNECTION TEST ---
        try {
            // Test Firebase Database connection
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("connectionTest")
            
            Log.d("FirebaseTest", "Testing Firebase Database connection...")
            
            myRef.setValue("Hello, Firebase! I am connected at ${System.currentTimeMillis()}")
                .addOnSuccessListener {
                    Log.d("FirebaseTest", "✅ SUCCESS: Wrote to Firebase Database!")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseTest", "❌ FAILURE: Could not write to Firebase Database.", e)
                }
            
            // Test Firebase Auth connection
            val auth = FirebaseAuth.getInstance()
            Log.d("FirebaseTest", "✅ Firebase Auth initialized")
            Log.d("FirebaseTest", "Current user: ${auth.currentUser?.email ?: "No user signed in"}")
            
        } catch (e: Exception) {
            Log.e("FirebaseTest", "❌ Firebase initialization failed", e)
        }
        // --- END: FIREBASE CONNECTION TEST ---
        
        enableEdgeToEdge()
        setContent {
            UserPanelNewTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp()
                }
            }
        }
        
        // Handle initial intent
        handleIntent(intent)
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }
    
    private fun handleIntent(intent: Intent) {
        val navigateToTracking = intent.getBooleanExtra("navigate_to_tracking", false)
        val trackingBusId = intent.getStringExtra("tracking_bus_id")
        
        if (navigateToTracking && !trackingBusId.isNullOrEmpty()) {
            Log.d("MainActivity", "Navigating to tracking screen for bus: $trackingBusId")
            CoroutineScope(Dispatchers.Main).launch {
                NavigationEventBus.emit(NavigationEvent.NavigateToTracking(trackingBusId))
            }
        }
    }
}