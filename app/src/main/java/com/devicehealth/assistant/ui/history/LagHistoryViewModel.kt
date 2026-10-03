package com.devicehealth.assistant.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devicehealth.assistant.data.AppDatabase
import com.devicehealth.assistant.data.DeviceHealthRepository
import com.devicehealth.assistant.data.LagEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LagHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val healthRepo = DeviceHealthRepository(application)

    val lagEvents: StateFlow<List<LagEvent>> = db.lagDao().getAllEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun reportLag() {
        viewModelScope.launch(Dispatchers.IO) {
            val stats = healthRepo.getHealthSnapshot()
            db.lagDao().insertEvent(
                LagEvent(
                    timestamp = System.currentTimeMillis(),
                    ramAvailableBytes = stats.ramAvailableBytes,
                    ramLowMemory = stats.ramLowMemory,
                    storageAvailableBytes = stats.storageAvailableBytes,
                    uptimeMillis = stats.uptimeMillis
                )
            )
        }
    }
}
