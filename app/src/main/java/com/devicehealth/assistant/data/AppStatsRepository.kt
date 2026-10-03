package com.devicehealth.assistant.data

import android.app.AppOpsManager
import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Process
import android.os.UserHandle

data class AppInfo(
    val packageName: String,
    val name: String,
    val appBytes: Long,
    val dataBytes: Long,
    val cacheBytes: Long,
    val totalBytes: Long
)

class AppStatsRepository(private val context: Context) {

    fun hasUsageAccess(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun getAppStats(): List<AppInfo> {
        val pm = context.packageManager
        val storageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
        
        val apps = pm.getInstalledApplications(PackageManager.MATCH_DISABLED_COMPONENTS or PackageManager.MATCH_UNINSTALLED_PACKAGES)
        
        val result = mutableListOf<AppInfo>()
        for (app in apps) {
            // Filter to user apps
            if ((app.flags and ApplicationInfo.FLAG_SYSTEM) != 0 && (app.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
                continue
            }
            
            try {
                val stats = storageStatsManager.queryStatsForPackage(
                    app.storageUuid,
                    app.packageName,
                    UserHandle.getUserHandleForUid(app.uid)
                )
                
                val name = pm.getApplicationLabel(app).toString()
                val appBytes = stats.appBytes
                val dataBytes = stats.dataBytes
                val cacheBytes = stats.cacheBytes
                val totalBytes = appBytes + dataBytes + cacheBytes
                
                result.add(
                    AppInfo(
                        packageName = app.packageName,
                        name = name,
                        appBytes = appBytes,
                        dataBytes = dataBytes,
                        cacheBytes = cacheBytes,
                        totalBytes = totalBytes
                    )
                )
            } catch (e: Exception) {
                // Package might have been removed or query failed
            }
        }
        
        return result.sortedByDescending { it.totalBytes }
    }
}
