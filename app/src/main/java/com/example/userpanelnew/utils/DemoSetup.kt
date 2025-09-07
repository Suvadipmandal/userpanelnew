package com.example.userpanelnew.utils

import android.util.Log
import com.example.userpanelnew.services.DemoTrackingService

/**
 * Utility class to help set up demo tracking data
 */
object DemoSetup {
    
    private const val TAG = "DemoSetup"
    
    /**
     * Initialize demo tracking data
     * Call this when the app starts to ensure demo data is ready
     */
    fun initializeDemoData() {
        try {
            Log.d(TAG, "Initializing demo tracking data...")
            
            // Demo data is automatically initialized in DemoTrackingService
            // This function can be used to perform any additional setup
            
            Log.d(TAG, "Demo tracking data initialized successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing demo data", e)
        }
    }
    
    /**
     * Get available demo bus IDs
     */
    fun getAvailableDemoBusIds(): List<String> {
        return DemoTrackingService().getAvailableBusIds()
    }
    
    /**
     * Reset demo data for a specific bus
     */
    fun resetBusDemoData(busId: String) {
        DemoTrackingService().resetBusDemoData(busId)
        Log.d(TAG, "Reset demo data for bus: $busId")
    }
    
    /**
     * Get demo bus information
     */
    fun getDemoBusInfo(): Map<String, String> {
        return mapOf(
            "BUS101" to "Route 101 - Downtown to University",
            "BUS102" to "Route 102 - Airport to City Center", 
            "BUS103" to "Route 103 - Mall to Hospital",
            "BUS104" to "Route 104 - Station to Park"
        )
    }
}
