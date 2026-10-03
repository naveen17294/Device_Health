package com.devicehealth.assistant.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lag_events")
data class LagEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val ramAvailableBytes: Long,
    val ramLowMemory: Boolean,
    val storageAvailableBytes: Long,
    val uptimeMillis: Long,
    val suspectedPackageName: String? = null
)
