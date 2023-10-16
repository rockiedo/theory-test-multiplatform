package com.rdev.tt.ui

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

sealed interface BusEvent {
    data object HistoryChanged : BusEvent
}

/**
 * Dedicated event bus for navigation events, especially navigation results.
 */
class NavigationBus {
    private val _eventStream = MutableSharedFlow<BusEvent>()
    val eventStream: SharedFlow<BusEvent> = _eventStream

    suspend fun emit(event: BusEvent) {
        _eventStream.emit(event)
    }
}