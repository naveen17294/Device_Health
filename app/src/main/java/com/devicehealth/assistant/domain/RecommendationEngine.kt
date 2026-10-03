package com.devicehealth.assistant.domain

import com.devicehealth.assistant.data.AppInfo
import com.devicehealth.assistant.data.DeviceHealthState
import com.devicehealth.assistant.data.LagEvent

enum class Severity { INFO, WARNING, CRITICAL }

data class Recommendation(
    val id: String,
    val title: String,
    val message: String,
    val severity: Severity,
    val explanation: String
)

class RecommendationEngine {
    fun evaluate(
        healthState: DeviceHealthState,
        lagEvents: List<LagEvent>,
        apps: List<AppInfo>,
        largeAppThresholdBytes: Long,
        lowStorageWarningPercent: Int
    ): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()
        
        val storageUsed = healthState.storageTotalBytes - healthState.storageAvailableBytes
        val freePercent = 100 - (storageUsed.toDouble() / healthState.storageTotalBytes * 100).toInt()
        
        if (freePercent < lowStorageWarningPercent) {
            recommendations.add(Recommendation(
                id = "low_storage",
                title = "Storage is critically low",
                message = "Remove downloads or large unused apps.",
                severity = Severity.CRITICAL,
                explanation = "Your device has less than $lowStorageWarningPercent% storage left. Low storage can cause system instability and prevent apps from updating."
            ))
        }

        if (healthState.ramLowMemory) {
            recommendations.add(Recommendation(
                id = "low_memory",
                title = "Memory Pressure Detected",
                message = "Android currently reports memory pressure.",
                severity = Severity.WARNING,
                explanation = "The OS is struggling to keep apps in memory. Closing heavy background applications may help."
            ))
        }
        
        val uptimeDays = healthState.uptimeMillis / (1000 * 60 * 60 * 24)
        val oneDayAgo = System.currentTimeMillis() - (1000 * 60 * 60 * 24)
        val recentLagCount = lagEvents.count { it.timestamp > oneDayAgo }
        
        if (uptimeDays >= 7 && recentLagCount >= 3) {
            recommendations.add(Recommendation(
                id = "restart_device",
                title = "Device needs a restart",
                message = "Restarting may clear a temporary system or app issue.",
                severity = Severity.WARNING,
                explanation = "Your phone has been running for $uptimeDays days and you've reported $recentLagCount lag events recently. A reboot clears caches and resets background processes."
            ))
        }

        val largeApps = apps.filter { it.totalBytes >= largeAppThresholdBytes }
        if (largeApps.isNotEmpty()) {
            val appNames = largeApps.take(3).joinToString { it.name }
            recommendations.add(Recommendation(
                id = "large_apps",
                title = "Large Apps Found",
                message = "Review offline content for: $appNames",
                severity = Severity.INFO,
                explanation = "These apps exceed your configured size threshold. Consider clearing their cache or deleting downloaded media."
            ))
        }

        return recommendations
    }
}
