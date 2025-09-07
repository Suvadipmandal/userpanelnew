package com.example.userpanelnew.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Event bus for handling navigation events from notifications
 */
object NavigationEventBus {
    
    private val _events = MutableSharedFlow<NavigationEvent>()
    val events: SharedFlow<NavigationEvent> = _events.asSharedFlow()
    
    suspend fun emit(event: NavigationEvent) {
        _events.emit(event)
    }
}

sealed class NavigationEvent {
    data class NavigateToTracking(val busId: String) : NavigationEvent()
}
