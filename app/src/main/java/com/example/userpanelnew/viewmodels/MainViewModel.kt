package com.example.userpanelnew.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userpanelnew.data.DummyDataRepository
import com.example.userpanelnew.models.*
import com.example.userpanelnew.services.FirebaseAuthService
import com.example.userpanelnew.services.GoogleSignInService
import com.example.userpanelnew.utils.LanguagePreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    
    private val repository = DummyDataRepository()
    private val firebaseAuthService = FirebaseAuthService()
    private var googleSignInService: GoogleSignInService? = null
    private var languagePreferenceManager: LanguagePreferenceManager? = null
    
    // Authentication state
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    // Current user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    // Buses data
    private val _buses = MutableStateFlow<List<Bus>>(emptyList())
    val buses: StateFlow<List<Bus>> = _buses.asStateFlow()
    
    // Bus stops data
    private val _busStops = MutableStateFlow<List<BusStop>>(emptyList())
    val busStops: StateFlow<List<BusStop>> = _busStops.asStateFlow()
    
    // Selected bus for tracking
    private val _selectedBus = MutableStateFlow<Bus?>(null)
    val selectedBus: StateFlow<Bus?> = _selectedBus.asStateFlow()
    
    // App language
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()
    
    // Language change callback
    private var _onLanguageChanged: ((AppLanguage) -> Unit)? = null
    val onLanguageChanged: ((AppLanguage) -> Unit)? get() = _onLanguageChanged
    
    // Location permission state
    private val _locationPermissionGranted = MutableStateFlow(false)
    val locationPermissionGranted: StateFlow<Boolean> = _locationPermissionGranted.asStateFlow()
    
    // Notification permission state
    private val _notificationPermissionGranted = MutableStateFlow(false)
    val notificationPermissionGranted: StateFlow<Boolean> = _notificationPermissionGranted.asStateFlow()
    
    init {
        try {
            loadDummyData()
        } catch (e: Exception) {
            // Handle initialization error gracefully
        }
    }
    
    fun initializeLanguagePreferenceManager(context: Context) {
        println("MainViewModel: Initializing language preference manager")
        languagePreferenceManager = LanguagePreferenceManager(context)
        googleSignInService = GoogleSignInService(context)
        
        // Debug: Log Google Sign-In service initialization
        android.util.Log.d("MainViewModel", "Google Sign-In service initialized: ${googleSignInService != null}")
        
        // Load saved language preference
        val savedLanguage = languagePreferenceManager?.getSavedLanguage() ?: AppLanguage.ENGLISH
        println("MainViewModel: Loaded saved language: ${savedLanguage.displayName}")
        _currentLanguage.value = savedLanguage
        println("MainViewModel: Current language set to: ${_currentLanguage.value.displayName}")
    }
    
    private fun loadDummyData() {
        viewModelScope.launch {
            try {
                _buses.value = repository.getBusesWithDelay()
                _busStops.value = repository.getBusStopsWithDelay()
            } catch (e: Exception) {
                // Handle data loading error gracefully
                _buses.value = emptyList()
                _busStops.value = emptyList()
            }
        }
    }
    
    fun login(email: String, password: String): Boolean {
        return try {
            // Simple dummy authentication
            if (email.isNotEmpty() && password.isNotEmpty()) {
                _currentUser.value = repository.getDummyUser()
                _isLoggedIn.value = true
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    fun register(name: String, email: String, phone: String, password: String): Boolean {
        return try {
            // Simple dummy registration
            if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()) {
                _currentUser.value = User(
                    id = "USER${System.currentTimeMillis()}",
                    name = name,
                    email = email,
                    phone = phone
                )
                _isLoggedIn.value = true
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            try {
                // Sign out from Firebase Auth if user is signed in
                if (firebaseAuthService.isUserSignedIn()) {
                    firebaseAuthService.signOut()
                }
                
                // Clear local state
                _isLoggedIn.value = false
                _currentUser.value = null
                _selectedBus.value = null
                
            } catch (e: Exception) {
                // Handle logout error gracefully - still clear local state
                _isLoggedIn.value = false
                _currentUser.value = null
                _selectedBus.value = null
            }
        }
    }
    
    fun selectBus(bus: Bus) {
        try {
            _selectedBus.value = bus
        } catch (e: Exception) {
            // Handle bus selection error gracefully
        }
    }
    
    fun clearSelectedBus() {
        try {
            _selectedBus.value = null
        } catch (e: Exception) {
            // Handle clear error gracefully
        }
    }
    
    fun setLanguage(language: AppLanguage) {
        try {
            println("MainViewModel: Setting language to ${language.displayName}")
            println("MainViewModel: Current language before change: ${_currentLanguage.value.displayName}")
            
            // Force StateFlow update
            _currentLanguage.value = language
            
            println("MainViewModel: Language value set to: ${_currentLanguage.value.displayName}")
            
            // Save to preferences
            languagePreferenceManager?.saveLanguage(language)
            
            // Trigger callback
            _onLanguageChanged?.invoke(language)
            
            println("MainViewModel: Language set successfully")
        } catch (e: Exception) {
            println("MainViewModel: Error setting language: ${e.message}")
            e.printStackTrace()
            // Handle language change error gracefully
        }
    }
    
    fun setLanguageChangeCallback(callback: (AppLanguage) -> Unit) {
        _onLanguageChanged = callback
    }
    
    fun getCurrentLanguage(): AppLanguage {
        return _currentLanguage.value
    }
    
    fun setLanguageAndRestart(language: AppLanguage) {
        _currentLanguage.value = language
        _onLanguageChanged?.invoke(language)
    }
    
    fun setLocationPermission(granted: Boolean) {
        try {
            _locationPermissionGranted.value = granted
        } catch (e: Exception) {
            // Handle permission change error gracefully
        }
    }
    
    fun setNotificationPermission(granted: Boolean) {
        try {
            _notificationPermissionGranted.value = granted
        } catch (e: Exception) {
            // Handle permission change error gracefully
        }
    }
    
    fun refreshBuses() {
        viewModelScope.launch {
            try {
                _buses.value = repository.getBusesWithDelay()
            } catch (e: Exception) {
                // Handle refresh error gracefully
            }
        }
    }
    
    fun refreshBusStops() {
        viewModelScope.launch {
            try {
                _busStops.value = repository.getBusStopsWithDelay()
            } catch (e: Exception) {
                // Handle refresh error gracefully
            }
        }
    }
    
    fun getGoogleSignInIntent(): android.content.Intent? {
        return googleSignInService?.getSignInIntent()
    }
    
    fun handleGoogleSignInResult(data: android.content.Intent?) {
        if (googleSignInService == null) {
            android.util.Log.e("MainViewModel", "GoogleSignInService is null")
            return
        }
        
        viewModelScope.launch {
            try {
                android.util.Log.d("MainViewModel", "Handling Google Sign-In result")
                
                val idTokenResult = googleSignInService!!.handleSignInResult(data)
                if (idTokenResult.isSuccess) {
                    val idToken = idTokenResult.getOrNull()
                    if (idToken != null) {
                        android.util.Log.d("MainViewModel", "Got ID token, authenticating with Firebase")
                        
                        val firebaseResult = firebaseAuthService.signInWithGoogle(idToken)
                        if (firebaseResult.isSuccess) {
                            val firebaseUser = firebaseResult.getOrNull()
                            if (firebaseUser != null) {
                                android.util.Log.d("MainViewModel", "Firebase authentication successful")
                                
                                // Create user object from Firebase user
                                _currentUser.value = User(
                                    id = firebaseUser.uid,
                                    name = firebaseUser.displayName ?: "Google User",
                                    email = firebaseUser.email ?: "",
                                    phone = firebaseUser.phoneNumber ?: ""
                                )
                                _isLoggedIn.value = true
                            } else {
                                android.util.Log.e("MainViewModel", "Firebase user is null")
                            }
                        } else {
                            android.util.Log.e("MainViewModel", "Firebase authentication failed: ${firebaseResult.exceptionOrNull()?.message}")
                        }
                    } else {
                        android.util.Log.e("MainViewModel", "ID token is null")
                    }
                } else {
                    android.util.Log.e("MainViewModel", "Google Sign-In failed: ${idTokenResult.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error in Google Sign-In flow: ${e.message}", e)
            }
        }
    }
}
