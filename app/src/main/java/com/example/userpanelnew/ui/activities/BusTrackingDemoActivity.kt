package com.example.userpanelnew.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.userpanelnew.ui.screens.BusTrackingScreen
import com.example.userpanelnew.ui.theme.UserPanelNewTheme

/**
 * Demo Activity for Bus Tracking Screen
 * 
 * This activity showcases the complete bus tracking functionality with:
 * - Material3 UI design
 * - Mapbox Maps integration
 * - Firebase Realtime Database
 * - Location permissions
 * - Demo bus simulation
 */
class BusTrackingDemoActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            UserPanelNewTheme {
                BusTrackingDemoScreen(
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusTrackingDemoScreen(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showInfo by remember { mutableStateOf(false) }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Main Bus Tracking Screen
        BusTrackingScreen()
        
        // Top App Bar with back button and info
        TopAppBar(
            title = {
                Text(
                    text = "Bus Tracking Demo",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                TextButton(
                    onClick = { showInfo = true }
                ) {
                    Text("Info")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ),
            modifier = Modifier
        )
        
        // Info Dialog
        if (showInfo) {
            BusTrackingInfoDialog(
                onDismiss = { showInfo = false }
            )
        }
    }
}

@Composable
fun BusTrackingInfoDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Bus Tracking Demo",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "This demo showcases a complete bus tracking system with:",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                InfoItem("🎨 Material3 UI", "Modern, beautiful interface design")
                InfoItem("🗺️ Mapbox Maps", "Real-time map with custom markers")
                InfoItem("🔥 Firebase Database", "Live bus location updates every 2s")
                InfoItem("📍 Location Services", "User location with permission handling")
                InfoItem("🚌 Demo Simulation", "Bus moving around Vadodara Junction")
                InfoItem("📊 Real-time Data", "ETA, distance, and route information")
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Click 'Start Tracking' to begin the demo!",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Got it!")
            }
        },
        modifier = modifier
    )
}

@Composable
private fun InfoItem(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
