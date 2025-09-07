package com.example.userpanelnew.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.userpanelnew.utils.NotificationEventBus
import com.example.userpanelnew.utils.NotificationEvent
import com.example.userpanelnew.utils.NavigationEventBus
import com.example.userpanelnew.utils.NavigationEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BusTrackingNotificationReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "BusTrackingNotificationReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            BusTrackingNotificationService.ACTION_STOP_TRACKING -> {
                Log.d(TAG, "Stop tracking action received from notification")
                // Hide the notification
                val notificationService = BusTrackingNotificationService(context)
                notificationService.hideTrackingNotification()
                
                // Emit event to stop tracking
                CoroutineScope(Dispatchers.Main).launch {
                    NotificationEventBus.emit(NotificationEvent.StopTracking)
                }
            }
            BusTrackingNotificationService.ACTION_ENTER_TRACKING -> {
                Log.d(TAG, "Enter tracking action received from notification")
                val busId = intent.getStringExtra("tracking_bus_id") ?: ""
                Log.d(TAG, "Navigating to tracking screen for bus: $busId")
                
                // Emit navigation event to open tracking screen
                CoroutineScope(Dispatchers.Main).launch {
                    NavigationEventBus.emit(NavigationEvent.NavigateToTracking(busId))
                }
            }
            BusTrackingNotificationService.ACTION_OPEN_APP -> {
                Log.d(TAG, "Open app action received from notification")
                // The notification's content intent will handle opening the app
                CoroutineScope(Dispatchers.Main).launch {
                    NotificationEventBus.emit(NotificationEvent.OpenApp)
                }
            }
        }
    }
}
