package com.example.data.repository

import com.example.data.local.OutboxDao
import com.example.data.model.OutboxEvent
import com.example.data.model.SyncStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SyncEngine(
    private val outboxDao: OutboxDao,
    private val coroutineScope: CoroutineScope
) {
    private val _syncState = MutableStateFlow(SyncStatus.SYNCED)
    val syncState: StateFlow<SyncStatus> = _syncState.asStateFlow()

    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    fun toggleOfflineMode() {
        val newMode = !_isOfflineMode.value
        _isOfflineMode.value = newMode
        if (newMode) {
            _syncState.value = SyncStatus.OFFLINE_QUEUED
        } else {
            triggerSync()
        }
    }

    fun triggerSync() {
        if (_isOfflineMode.value) return
        coroutineScope.launch(Dispatchers.IO) {
            _syncState.value = SyncStatus.SYNCING
            delay(1200) // Simulate fast network handshake & outbox processing
            val pending = outboxDao.getPendingEvents()
            for (event in pending) {
                outboxDao.updateStatus(event.id, "SYNCED")
            }
            _syncState.value = SyncStatus.SYNCED
        }
    }
}
