package com.devicehealth.assistant.domain

import com.devicehealth.assistant.data.AppInfo
import com.devicehealth.assistant.data.DeviceHealthState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecommendationEngineTest {
    
    @Test
    fun evaluate_returns_low_storage_warning_when_below_threshold() {
        val engine = RecommendationEngine()
        val healthState = DeviceHealthState(
            ramTotalBytes = 1000,
            ramAvailableBytes = 500,
            ramLowMemory = false,
            storageTotalBytes = 100,
            storageAvailableBytes = 10, // 10% free
            uptimeMillis = 0
        )
        
        val recs = engine.evaluate(healthState, emptyList(), emptyList(), 500_000_000L, 15)
        
        assertTrue(recs.any { it.id == "low_storage" })
        assertEquals(Severity.CRITICAL, recs.first { it.id == "low_storage" }.severity)
    }

    @Test
    fun evaluate_returns_large_apps_info_when_apps_exceed_threshold() {
        val engine = RecommendationEngine()
        val healthState = DeviceHealthState(1000, 500, false, 100, 50, 0)
        
        val apps = listOf(
            AppInfo("com.big.app", "Big App", 600_000_000L, 0, 0, 600_000_000L)
        )
        
        val recs = engine.evaluate(healthState, emptyList(), apps, 500_000_000L, 15)
        
        assertTrue(recs.any { it.id == "large_apps" })
        assertEquals(Severity.INFO, recs.first { it.id == "large_apps" }.severity)
    }
}
