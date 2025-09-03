package com.example.userpanelnew.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.mapbox.geojson.Point
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationHelper(private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        try {
            LocationServices.getFusedLocationProviderClient(context)
        } catch (e: Exception) {
            throw RuntimeException("Failed to initialize FusedLocationProviderClient", e)
        }
    }
    
    fun hasLocationPermission(): Boolean {
        return try {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            false
        }
    }
    
    @Suppress("MissingPermission")
    suspend fun getCurrentLocation(): Point? {
        if (!hasLocationPermission()) {
            return null
        }
        
        return try {
            suspendCancellableCoroutine { continuation ->
                val cancellationToken = object : CancellationToken() {
                    override fun onCanceledRequested(listener: OnTokenCanceledListener): CancellationToken {
                        return this
                    }
                    
                    override fun isCancellationRequested(): Boolean = false
                }
                
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationToken
                ).addOnSuccessListener { location ->
                    try {
                        location?.let {
                            val point = Point.fromLngLat(it.longitude, it.latitude)
                            continuation.resume(point)
                        } ?: continuation.resume(null)
                    } catch (e: Exception) {
                        continuation.resume(null)
                    }
                }.addOnFailureListener {
                    continuation.resume(null)
                }
            }
        } catch (e: Exception) {
            null
        }
    }
    
    fun getDefaultLocation(): Point {
        // Default to a location in India (Vadodara area as per the dummy data)
        return try {
            Point.fromLngLat(73.1812, 22.3072)
        } catch (e: Exception) {
            // Fallback to a safe default if Point creation fails
            Point.fromLngLat(0.0, 0.0)
        }
    }
}
