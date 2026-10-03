package com.devicehealth.assistant.data

import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import android.os.StatFs
import android.os.SystemClock

class DeviceHealthRepository(private val context: Context) {
    fun getHealthSnapshot(): DeviceHealthState {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val statFs = StatFs(Environment.getDataDirectory().path)
        
        return DeviceHealthState(
            ramTotalBytes = memoryInfo.totalMem,
            ramAvailableBytes = memoryInfo.availMem,
            ramLowMemory = memoryInfo.lowMemory,
            storageTotalBytes = statFs.totalBytes,
            storageAvailableBytes = statFs.availableBytes,
            uptimeMillis = SystemClock.elapsedRealtime()
        )
    }
}

data class DeviceHealthState(
    val ramTotalBytes: Long,
    val ramAvailableBytes: Long,
    val ramLowMemory: Boolean,
    val storageTotalBytes: Long,
    val storageAvailableBytes: Long,
    val uptimeMillis: Long
)
