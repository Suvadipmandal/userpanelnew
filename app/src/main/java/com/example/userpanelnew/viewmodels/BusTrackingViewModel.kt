package com.example.userpanelnew.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userpanelnew.models.*
import com.example.userpanelnew.services.BusTrackingService
import com.example.userpanelnew.services.DemoTrackingService
import com.example.userpanelnew.services.FirebaseBusSimulator
import com.example.userpanelnew.services.BusTrackingNotificationService
import com.example.userpanelnew.utils.LocationHelper
import com.example.userpanelnew.utils.FirebaseSetupHelper
import com.example.userpanelnew.utils.NotificationEventBus
import com.example.userpanelnew.utils.NotificationEvent
import com.mapbox.geojson.Point
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.*

class BusTrackingViewModel : ViewModel() {
    
    private val busTrackingService = BusTrackingService()
    private val demoTrackingService = DemoTrackingService()
    private val firebaseSetupHelper = FirebaseSetupHelper()
    private var locationHelper: LocationHelper? = null
    private var notificationService: BusTrackingNotificationService? = null
    private var useDemoData = true // Set to false when real Firebase data is available
    
    companion object {
        private const val TAG = "BusTrackingViewModel"
        private const val LOCATION_UPDATE_INTERVAL = 5000L // 5 seconds
    }
    
    // Tracking state
    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()
    
    // Current tracking session
    private val _currentSession = MutableStateFlow<TrackingSession?>(null)
    val currentSession: StateFlow<TrackingSession?> = _currentSession.asStateFlow()
    
    // Bus location updates
    private val _busLocation = MutableStateFlow<BusLocation?>(null)
    val busLocation: StateFlow<BusLocation?> = _busLocation.asStateFlow()
    
    // User location
    private val _userLocation = MutableStateFlow<UserLocation?>(null)
    val userLocation: StateFlow<UserLocation?> = _userLocation.asStateFlow()
    
    // Tracking notification state
    private val _notificationState = MutableStateFlow(TrackingNotificationState())
    val notificationState: StateFlow<TrackingNotificationState> = _notificationState.asStateFlow()
    
    // Map state
    private val _shouldFitBothMarkers = MutableStateFlow(false)
    val shouldFitBothMarkers: StateFlow<Boolean> = _shouldFitBothMarkers.asStateFlow()
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Private variables for tracking
    private var busLocationJob: Job? = null
    private var userLocationJob: Job? = null
    private var sessionUpdateJob: Job? = null
    
    /**
     * Initialize location helper with context
     */
    fun initializeLocationHelper(context: android.content.Context) {
        locationHelper = LocationHelper(context)
        notificationService = BusTrackingNotificationService(context)
        
        // Setup notification event listeners
        setupNotificationEventListeners()
        
        // Initialize Firebase demo data when location helper is set up
        viewModelScope.launch {
            try {
                firebaseSetupHelper.initializeDemoData()
                firebaseSetupHelper.startBusSimulation()
                Log.d(TAG, "Firebase demo data initialized and simulation started")
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing Firebase demo data", e)
                _error.value = "Failed to initialize Firebase demo data"
            }
        }
    }
    
    /**
     * Start tracking a bus
     */
    fun startTracking(busId: String, userId: String) {
        if (_isTracking.value) {
            Log.w(TAG, "Already tracking a bus")
            return
        }
        
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting bus tracking for: $busId")
                
                // Get current user location
                val currentUserLocation = getCurrentUserLocation()
                if (currentUserLocation == null) {
                    _error.value = "Unable to get your current location"
                    return@launch
                }
                
                // Create tracking session (use demo service for instant results)
                val sessionResult = if (useDemoData) {
                    demoTrackingService.createTrackingSession(
                        userId = userId,
                        busId = busId,
                        userLocation = currentUserLocation
                    )
                } else {
                    busTrackingService.createTrackingSession(
                        userId = userId,
                        busId = busId,
                        userLocation = currentUserLocation
                    )
                }
                
                if (sessionResult.isFailure) {
                    _error.value = "Failed to start tracking session"
                    return@launch
                }
                
                val session = sessionResult.getOrThrow()
                _currentSession.value = session
                _isTracking.value = true
                
                // Update notification state
                updateNotificationState(busId, currentUserLocation)
                
                // Show notification
                notificationService?.showTrackingNotification(_notificationState.value)
                
                // Start location updates
                startLocationUpdates(busId)
                
                // Fit camera to both markers
                _shouldFitBothMarkers.value = true
                
                Log.d(TAG, "Bus tracking started successfully")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error starting bus tracking", e)
                _error.value = "Failed to start tracking: ${e.message}"
            }
        }
    }
    
    /**
     * Stop tracking
     */
    fun stopTracking() {
        if (!_isTracking.value) {
            Log.w(TAG, "Not currently tracking any bus")
            return
        }
        
        viewModelScope.launch {
            try {
                Log.d(TAG, "Stopping bus tracking")
                
                // Cancel all jobs
                busLocationJob?.cancel()
                userLocationJob?.cancel()
                sessionUpdateJob?.cancel()
                
                // End tracking session
                _currentSession.value?.let { session ->
                    if (useDemoData) {
                        demoTrackingService.endTrackingSession(session.sessionId)
                    } else {
                        busTrackingService.endTrackingSession(session.sessionId)
                    }
                }
                
                // Hide notification
                notificationService?.hideTrackingNotification()
                
                // Reset state
                _isTracking.value = false
                _currentSession.value = null
                _busLocation.value = null
                _notificationState.value = TrackingNotificationState()
                _shouldFitBothMarkers.value = false
                
                Log.d(TAG, "Bus tracking stopped successfully")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping bus tracking", e)
                _error.value = "Failed to stop tracking: ${e.message}"
            }
        }
    }
    
    /**
     * Start location updates
     */
    private fun startLocationUpdates(busId: String) {
        // Start bus location updates (use demo service for instant results)
        busLocationJob = viewModelScope.launch {
            val locationFlow = if (useDemoData) {
                demoTrackingService.getBusLocationUpdates(busId)
            } else {
                busTrackingService.getBusLocationUpdates(busId)
            }
            
            locationFlow
                .catch { e ->
                    Log.e(TAG, "Error in bus location updates", e)
                    _error.value = "Failed to get bus location updates"
                }
                .collect { busLocation ->
                    _busLocation.value = busLocation
                    busLocation?.let { updateNotificationWithBusLocation(it) }
                }
        }
        
        // Start user location updates
        userLocationJob = viewModelScope.launch {
            while (_isTracking.value) {
                try {
                    val userLocation = getCurrentUserLocation()
                    if (userLocation != null) {
                        _userLocation.value = userLocation
                        updateNotificationWithUserLocation(userLocation)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error getting user location", e)
                }
                
                delay(LOCATION_UPDATE_INTERVAL)
            }
        }
        
        // Start session updates
        sessionUpdateJob = viewModelScope.launch {
            while (_isTracking.value) {
                try {
                    val session = _currentSession.value
                    val busLocation = _busLocation.value
                    val userLocation = _userLocation.value
                    
                    if (session != null && busLocation != null && userLocation != null) {
                        if (useDemoData) {
                            demoTrackingService.updateTrackingSession(
                                session.sessionId,
                                userLocation,
                                busLocation
                            )
                        } else {
                            busTrackingService.updateTrackingSession(
                                session.sessionId,
                                userLocation,
                                busLocation
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating tracking session", e)
                }
                
                delay(LOCATION_UPDATE_INTERVAL)
            }
        }
    }
    
    /**
     * Get current user location
     */
    private suspend fun getCurrentUserLocation(): UserLocation? {
        return try {
            val helper = locationHelper ?: return null
            val location = helper.getCurrentLocation()
            UserLocation(
                latitude = location?.latitude() ?: 0.0,
                longitude = location?.longitude() ?: 0.0,
                accuracy = 50.0, // Default accuracy
                timestamp = com.google.firebase.Timestamp.now()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user location", e)
            null
        }
    }
    
    /**
     * Update notification state with bus info
     */
    private fun updateNotificationState(busId: String, userLocation: UserLocation) {
        _notificationState.value = TrackingNotificationState(
            isVisible = true,
            busId = busId,
            busRoute = "", // Will be updated when bus location is received
            eta = 0,
            distance = 0.0,
            isLive = true
        )
    }
    
    /**
     * Update notification with bus location
     */
    private fun updateNotificationWithBusLocation(busLocation: BusLocation) {
        val currentState = _notificationState.value
        val userLocation = _userLocation.value
        
        if (userLocation != null) {
            val distance = calculateDistance(
                userLocation.latitude, userLocation.longitude,
                busLocation.latitude, busLocation.longitude
            )
            
            val eta = busTrackingService.calculateETA(distance, busLocation.speed)
            
            _notificationState.value = currentState.copy(
                busRoute = busLocation.route,
                eta = eta,
                distance = distance
            )
            
            // Update notification
            notificationService?.updateTrackingNotification(_notificationState.value)
        }
    }
    
    /**
     * Update notification with user location
     */
    private fun updateNotificationWithUserLocation(userLocation: UserLocation) {
        val currentState = _notificationState.value
        val busLocation = _busLocation.value
        
        if (busLocation != null) {
            val distance = calculateDistance(
                userLocation.latitude, userLocation.longitude,
                busLocation.latitude, busLocation.longitude
            )
            
            val eta = busTrackingService.calculateETA(distance, busLocation.speed)
            
            _notificationState.value = currentState.copy(
                eta = eta,
                distance = distance
            )
            
            // Update notification
            notificationService?.updateTrackingNotification(_notificationState.value)
        }
    }
    
    /**
     * Calculate distance between two points
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371000.0 // Earth's radius in meters
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return earthRadius * c
    }
    
    /**
     * Clear error
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Reset fit markers flag
     */
    fun resetFitMarkersFlag() {
        _shouldFitBothMarkers.value = false
    }
    
    /**
     * Get formatted distance
     */
    fun getFormattedDistance(): String {
        val distance = _notificationState.value.distance
        return if (useDemoData) {
            demoTrackingService.formatDistance(distance)
        } else {
            busTrackingService.formatDistance(distance)
        }
    }
    
    /**
     * Switch to real Firebase data (call this when you have real data)
     */
    fun enableRealFirebaseData() {
        useDemoData = false
        Log.d(TAG, "Switched to real Firebase data")
    }
    
    /**
     * Check if using demo data
     */
    fun isUsingDemoData(): Boolean {
        return useDemoData
    }
    
    /**
     * Setup notification event listeners
     */
    private fun setupNotificationEventListeners() {
        viewModelScope.launch {
            NotificationEventBus.events.collect { event ->
                when (event) {
                    is NotificationEvent.StopTracking -> {
                        Log.d(TAG, "Received stop tracking event from notification")
                        stopTracking()
                    }
                    is NotificationEvent.OpenApp -> {
                        Log.d(TAG, "Received open app event from notification")
                        // App will be opened by the notification's content intent
                    }
                }
            }
        }
    }
    
    /**
     * Stop tracking from notification (called by broadcast receiver)
     */
    fun stopTrackingFromNotification() {
        Log.d(TAG, "Stopping tracking from notification")
        stopTracking()
    }
    
    override fun onCleared() {
        super.onCleared()
        busLocationJob?.cancel()
        userLocationJob?.cancel()
        sessionUpdateJob?.cancel()
        
        // Stop Firebase simulation when ViewModel is cleared
        firebaseSetupHelper.stopBusSimulation()
    }
}

