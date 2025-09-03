package com.example.userpanelnew

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MyMapUiState(
    val currentLocation: Location? = null,
    val hasLocationPermission: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class MyMapViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(MyMapUiState())
    val uiState: StateFlow<MyMapUiState> = _uiState.asStateFlow()
    
    private var fusedLocationClient: FusedLocationProviderClient? = null
    
    fun startLocationUpdates() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Location updates will be handled by the Mapbox location plugin
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    hasLocationPermission = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun updateLocation(location: Location) {
        _uiState.value = _uiState.value.copy(currentLocation = location)
    }
    
    fun updatePermissionStatus(hasPermission: Boolean) {
        _uiState.value = _uiState.value.copy(hasLocationPermission = hasPermission)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
