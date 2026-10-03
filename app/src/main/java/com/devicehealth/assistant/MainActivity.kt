package com.devicehealth.assistant

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.metrics.performance.JankStats
import com.devicehealth.assistant.theme.DeviceHealthTheme

class MainActivity : ComponentActivity() {
  private lateinit var jankStats: JankStats

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val listener = JankStats.OnFrameListener { frameData ->
        if (frameData.isJank) {
            Log.w("JankStats", "Jank detected: ${frameData.frameDurationUiNanos / 1000000} ms")
        }
    }
    jankStats = JankStats.createAndTrack(window, listener)

    setContent {
      DeviceHealthTheme { 
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { 
          MainNavigation() 
        } 
      }
    }
  }

  override fun onResume() {
      super.onResume()
      jankStats.isTrackingEnabled = true
  }

  override fun onPause() {
      super.onPause()
      jankStats.isTrackingEnabled = false
  }
}
