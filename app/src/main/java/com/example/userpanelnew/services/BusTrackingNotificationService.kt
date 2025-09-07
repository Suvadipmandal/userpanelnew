package com.example.userpanelnew.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.userpanelnew.MainActivity
import com.example.userpanelnew.R
import com.example.userpanelnew.models.TrackingNotificationState

class BusTrackingNotificationService(private val context: Context) {
    
    companion object {
        private const val CHANNEL_ID = "bus_tracking_channel"
        private const val CHANNEL_NAME = "Bus Tracking"
        private const val CHANNEL_DESCRIPTION = "Real-time bus tracking notifications"
        private const val NOTIFICATION_ID = 1001

        const val ACTION_STOP_TRACKING = "stop_tracking"
        const val ACTION_ENTER_TRACKING = "enter_tracking"
        const val ACTION_OPEN_APP = "open_app"
    }
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    init {
        createNotificationChannel()
    }
    
    /**
     * Create notification channel for Android 8.0+
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = CHANNEL_DESCRIPTION
                setShowBadge(false)
                enableLights(false)
                enableVibration(false)
                setSound(null, null)
            }
            
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Show tracking notification
     */
    fun showTrackingNotification(notificationState: TrackingNotificationState) {
        val notification = buildNotification(notificationState)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    /**
     * Update tracking notification
     */
    fun updateTrackingNotification(notificationState: TrackingNotificationState) {
        val notification = buildNotification(notificationState)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    /**
     * Hide tracking notification
     */
    fun hideTrackingNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }
    
    /**
     * Build notification with tracking details
     */
    private fun buildNotification(notificationState: TrackingNotificationState): Notification {
        // Create intent to open the app and navigate to tracking screen
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to_tracking", true)
            putExtra("tracking_bus_id", notificationState.busId)
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Create intent to stop tracking
        val stopTrackingIntent = Intent(context, BusTrackingNotificationReceiver::class.java).apply {
            action = ACTION_STOP_TRACKING
        }
        val stopTrackingPendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            stopTrackingIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Create intent to enter tracking screen
        val enterTrackingIntent = Intent(context, BusTrackingNotificationReceiver::class.java).apply {
            action = ACTION_ENTER_TRACKING
            putExtra("tracking_bus_id", notificationState.busId)
        }
        val enterTrackingPendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            enterTrackingIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Format distance and ETA
        val distanceText = formatDistance(notificationState.distance)
        val etaText = formatETA(notificationState.eta)
        
        // Build notification content
        val title = "🚌 Tracking Bus ${notificationState.busId}"
        val contentText = if (notificationState.busRoute.isNotEmpty()) {
            "Route: ${notificationState.busRoute} • $distanceText • ETA: $etaText"
        } else {
            "Distance: $distanceText • ETA: $etaText"
        }
        
        val bigText = buildString {
            appendLine("🚌 Bus ${notificationState.busId}")
            if (notificationState.busRoute.isNotEmpty()) {
                appendLine("📍 Route: ${notificationState.busRoute}")
            }
            appendLine("📏 Distance: $distanceText")
            appendLine("⏰ ETA: $etaText")
            appendLine("🔄 Live tracking active")
        }
        
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_bus_notification) // You'll need to add this icon
            .setContentTitle(title)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true) // Makes notification persistent
            .setAutoCancel(false)
            .setContentIntent(openAppPendingIntent)
            .addAction(
                R.drawable.ic_enter_tracking, // You'll need to add this icon
                "Enter Tracking",
                enterTrackingPendingIntent
            )
            .addAction(
                R.drawable.ic_stop, // You'll need to add this icon
                "Stop Tracking",
                stopTrackingPendingIntent
            )
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }
    
    /**
     * Format distance for display
     */
    private fun formatDistance(distance: Double): String {
        return when {
            distance < 1000 -> "${distance.toInt()}m"
            distance < 10000 -> "${String.format("%.1f", distance / 1000)}km"
            else -> "${(distance / 1000).toInt()}km"
        }
    }
    
    /**
     * Format ETA for display
     */
    private fun formatETA(eta: Int): String {
        return when {
            eta < 1 -> "Arriving now"
            eta == 1 -> "1 min"
            eta < 60 -> "$eta mins"
            else -> {
                val hours = eta / 60
                val minutes = eta % 60
                if (minutes == 0) "${hours}h" else "${hours}h ${minutes}m"
            }
        }
    }
}
