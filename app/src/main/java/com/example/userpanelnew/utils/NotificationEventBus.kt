package com.example.userpanelnew.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Simple event bus for handling notification actions
 */
object NotificationEventBus {
    
    private val _events = MutableSharedFlow<NotificationEvent>()
    val events: SharedFlow<NotificationEvent> = _events.asSharedFlow()
    
    suspend fun emit(event: NotificationEvent) {
        _events.emit(event)
    }
}

sealed class NotificationEvent {
    object StopTracking : NotificationEvent()
    object OpenApp : NotificationEvent()
}
