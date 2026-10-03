package com.devicehealth.assistant.worker

import android.service.quicksettings.TileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.devicehealth.assistant.data.AppDatabase
import com.devicehealth.assistant.data.DeviceHealthRepository
import com.devicehealth.assistant.data.LagEvent

class LagTileService : TileService() {
    
    private val scope = CoroutineScope(Dispatchers.IO)
    
    override fun onClick() {
        super.onClick()
        
        val healthRepo = DeviceHealthRepository(this)
        val db = AppDatabase.getDatabase(this)
        
        scope.launch {
            val stats = healthRepo.getHealthSnapshot()
            
            db.lagDao().insertEvent(LagEvent(
                timestamp = System.currentTimeMillis(),
                ramAvailableBytes = stats.ramAvailableBytes,
                ramLowMemory = stats.ramLowMemory,
                storageAvailableBytes = stats.storageAvailableBytes,
                uptimeMillis = stats.uptimeMillis
            ))
        }
    }
}
