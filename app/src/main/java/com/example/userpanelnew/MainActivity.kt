package com.example.userpanelnew

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.userpanelnew.ui.MainApp
import com.example.userpanelnew.ui.theme.UserPanelNewTheme

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
    }
}