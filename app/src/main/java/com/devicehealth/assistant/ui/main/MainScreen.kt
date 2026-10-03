package com.devicehealth.assistant.ui.main

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
      Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
      Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text("Error loading data", color = MaterialTheme.colorScheme.error)
      }
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
  val scrollState = rememberScrollState()

  Column(
      modifier = modifier
          .fillMaxSize()
          .verticalScroll(scrollState)
          .padding(24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
      Text(
          text = "Device Health", 
          style = MaterialTheme.typography.headlineLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(bottom = 8.dp)
      )

      val ramUsed = data.ramTotalBytes - data.ramAvailableBytes
      val ramPercent = if (data.ramTotalBytes > 0) ramUsed.toFloat() / data.ramTotalBytes.toFloat() else 0f
      
      MetricCard(
          title = "Memory (RAM)",
          icon = Icons.Rounded.Memory,
          usedStr = formatBytes(ramUsed),
          totalStr = formatBytes(data.ramTotalBytes),
          progress = ramPercent,
          isWarning = data.ramLowMemory,
          warningText = "Android reports memory pressure"
      )

      val storageUsed = data.storageTotalBytes - data.storageAvailableBytes
      val storagePercent = if (data.storageTotalBytes > 0) storageUsed.toFloat() / data.storageTotalBytes.toFloat() else 0f
      val freePercent = ((1f - storagePercent) * 100).toInt()
      
      MetricCard(
          title = "Internal Storage",
          icon = Icons.Rounded.Storage,
          usedStr = formatBytes(storageUsed),
          totalStr = formatBytes(data.storageTotalBytes),
          progress = storagePercent,
          isWarning = freePercent < warningPercent,
          warningText = "Storage is critically low (< $warningPercent%)"
      )

      ElevatedCard(modifier = Modifier.fillMaxWidth()) {
          Row(
              modifier = Modifier.padding(16.dp),
              verticalAlignment = Alignment.CenterVertically
          ) {
              Icon(Icons.Rounded.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(32.dp))
              Spacer(modifier = Modifier.width(16.dp))
              Column {
                  val uptimeHours = data.uptimeMillis / (1000 * 60 * 60)
                  Text("Device Uptime", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                  Text("$uptimeHours hours", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
          }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text("Tools & Analysis", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          ActionCard(
              title = "App Analysis",
              icon = Icons.Rounded.Apps,
              modifier = Modifier.weight(1f),
              onClick = { onItemClick(AppAnalysis) }
          )
          ActionCard(
              title = "Lag History",
              icon = Icons.Rounded.History,
              modifier = Modifier.weight(1f),
              onClick = { onItemClick(com.devicehealth.assistant.LagHistory) }
          )
      }

      Button(
          onClick = { onItemClick(com.devicehealth.assistant.Recommendations) }, 
          modifier = Modifier.fillMaxWidth().height(56.dp),
          shape = MaterialTheme.shapes.large
      ) {
          Icon(Icons.Rounded.Lightbulb, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Smart Recommendations", style = MaterialTheme.typography.titleMedium)
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          OutlinedButton(
              onClick = onRefresh, 
              modifier = Modifier.weight(1f).height(50.dp)
          ) {
              Icon(Icons.Rounded.Refresh, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Refresh")
          }
          FilledTonalButton(
              onClick = {
                  try {
                      context.startActivity(Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS))
                  } catch (e: Exception) {
                      context.startActivity(Intent(Settings.ACTION_SETTINGS))
                  }
              }, 
              modifier = Modifier.weight(1f).height(50.dp)
          ) {
              Icon(Icons.Rounded.Settings, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Settings")
          }
      }
  }
}

@Composable
fun ActionCard(title: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    icon: ImageVector,
    usedStr: String,
    totalStr: String,
    progress: Float,
    isWarning: Boolean,
    warningText: String
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            val progressColor = if (isWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(usedStr, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(totalStr, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (isWarning) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(warningText, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

private fun formatBytes(bytes: Long): String {
    val gb = bytes / (1024.0 * 1024.0 * 1024.0)
    return String.format("%.2f GB", gb)
}
