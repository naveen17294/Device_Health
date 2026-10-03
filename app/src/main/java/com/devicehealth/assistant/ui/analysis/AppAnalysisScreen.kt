package com.devicehealth.assistant.ui.analysis

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devicehealth.assistant.data.AppInfo

@Composable
fun AppAnalysisScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AppAnalysisViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = onBack) { Text("Back") }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "App Analysis", style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(modifier = Modifier.height(16.dp))

        when (state) {
            AppAnalysisUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            AppAnalysisUiState.NeedsPermission -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Usage Access Required", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("We need Usage Access to read app storage sizes.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                    }) {
                        Text("Grant Permission")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { viewModel.checkPermissionAndLoad() }) {
                        Text("I've granted it, refresh")
                    }
                }
            }
            is AppAnalysisUiState.Error -> {
                Text("Error: ${(state as AppAnalysisUiState.Error).throwable.message}")
            }
            is AppAnalysisUiState.Success -> {
                val apps = (state as AppAnalysisUiState.Success).apps
                LazyColumn {
                    items(apps) { app ->
                        AppItemCard(app)
                    }
                }
            }
        }
    }
}

@Composable
fun AppItemCard(app: AppInfo) {
    val context = LocalContext.current
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(app.name, style = MaterialTheme.typography.titleMedium)
            Text(app.packageName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Total Size: ${formatBytes(app.totalBytes)}")
            Text("App: ${formatBytes(app.appBytes)} | Data: ${formatBytes(app.dataBytes)} | Cache: ${formatBytes(app.cacheBytes)}", style = MaterialTheme.typography.bodySmall)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Large app warning
            if (app.totalBytes > 500L * 1024 * 1024) { // 500 MB
                Text("⚠️ Very Large App - Review cache/downloads", color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${app.packageName}")
                }
                context.startActivity(intent)
            }) {
                Text("App Details")
            }
        }
    }
}

private fun formatBytes(bytes: Long): String {
    val mb = bytes / (1024.0 * 1024.0)
    return if (mb >= 1024.0) {
        String.format("%.2f GB", mb / 1024.0)
    } else {
        String.format("%.2f MB", mb)
    }
}
