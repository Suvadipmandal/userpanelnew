package com.example.userpanelnew

import android.app.Application
import android.util.Log
import com.mapbox.maps.MapInitOptions

class UserPanelApplication : Application() {
    
    companion object {
        private const val TAG = "UserPanelApplication"
    }
    
    override fun onCreate() {
        super.onCreate()
        // No need to set or validate Mapbox token here. It is picked up from AndroidManifest.xml.
    }
}
