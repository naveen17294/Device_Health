package com.devicehealth.assistant.ui.main

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.devicehealth.assistant.AppAnalysis
import com.devicehealth.assistant.data.DeviceHealthState

@Composable
fun MainScreen(
  onItemClick: (NavKey) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: MainScreenViewModel = viewModel()
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  when (state) {
    DeviceHealthUiState.Loading -> {
      Box(modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
          CircularProgressIndicator()
      }
    }
    is DeviceHealthUiState.Success -> {
      DashboardContent(
          data = (state as DeviceHealthUiState.Success).data,
          warningPercent = (state as DeviceHealthUiState.Success).warningPercent,
          onRefresh = { viewModel.refreshData() },
          onItemClick = onItemClick,
          modifier = modifier
      )
    }
    is DeviceHealthUiState.Error -> {
      Text("Error loading data: ${(state as DeviceHealthUiState.Error).throwable.message}")
    }
  }
}

@Composable
internal fun DashboardContent(
    data: DeviceHealthState, 
    warningPercent: Int,
    onRefresh: () -> Unit,
    onItemClick: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  Column(modifier = modifier.padding(16.dp).fillMaxSize()) {
      Text(text = "Device Health", style = MaterialTheme.typography.headlineMedium)
      Spacer(modifier = Modifier.height(16.dp))

      Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
          Column(modifier = Modifier.padding(16.dp)) {
              val ramUsed = data.ramTotalBytes - data.ramAvailableBytes
              Text("RAM", style = MaterialTheme.typography.titleMedium)
              Text("Used: ${formatBytes(ramUsed)} / ${formatBytes(data.ramTotalBytes)}")
              if (data.ramLowMemory) {
                  Text("⚠️ Android reports memory pressure", color = MaterialTheme.colorScheme.error)
              }
          }
      }

      Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
          Column(modifier = Modifier.padding(16.dp)) {
              val storageUsed = data.storageTotalBytes - data.storageAvailableBytes
              val storagePercentUsed = (storageUsed.toDouble() / data.storageTotalBytes * 100).toInt()
              val freePercent = 100 - storagePercentUsed
              Text("Storage", style = MaterialTheme.typography.titleMedium)
              Text("Used: ${formatBytes(storageUsed)} / ${formatBytes(data.storageTotalBytes)}")
              Text("Free space: ${freePercent}%")
              if (freePercent < warningPercent) {
                  Text("⚠️ Storage is critically low (< $warningPercent%)", color = MaterialTheme.colorScheme.error)
              }
          }
      }

      Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
          Column(modifier = Modifier.padding(16.dp)) {
              val uptimeHours = data.uptimeMillis / (1000 * 60 * 60)
              Text("Device Uptime", style = MaterialTheme.typography.titleMedium)
              Text("$uptimeHours hours")
          }
      }

      Spacer(modifier = Modifier.weight(1f))

      Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(onClick = { onItemClick(AppAnalysis) }, modifier = Modifier.weight(1f)) {
              Text("App Analysis")
          }
          Button(onClick = { onItemClick(com.devicehealth.assistant.LagHistory) }, modifier = Modifier.weight(1f)) {
              Text("Lag History")
          }
      }

      Button(
          onClick = { onItemClick(com.devicehealth.assistant.Recommendations) }, 
          modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
      ) {
          Text("View Recommendations")
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Button(onClick = onRefresh, modifier = Modifier.weight(1f).padding(end = 8.dp)) {
              Text("Refresh")
          }
          FilledTonalButton(onClick = {
              try {
                  context.startActivity(Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS))
              } catch (e: Exception) {
                  context.startActivity(Intent(Settings.ACTION_SETTINGS))
              }
          }, modifier = Modifier.weight(1f).padding(start = 8.dp)) {
              Text("Storage Settings")
          }
      }
  }
}

private fun formatBytes(bytes: Long): String {
    val gb = bytes / (1024.0 * 1024.0 * 1024.0)
    return String.format("%.2f GB", gb)
}
