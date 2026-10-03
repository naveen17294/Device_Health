package com.devicehealth.assistant.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devicehealth.assistant.data.LagEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LagHistoryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LagHistoryViewModel = viewModel()
) {
    val events by viewModel.lagEvents.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = onBack) { Text("Back") }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Lag History", style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { viewModel.reportLag() },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("Phone lagged just now!")
        }

        if (events.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No lag events reported yet.")
            }
        } else {
            LazyColumn {
                items(events) { event ->
                    LagEventCard(event)
                }
            }
        }
    }
}

@Composable
fun LagEventCard(event: LagEvent) {
    val formatter = SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault())
    val dateString = formatter.format(Date(event.timestamp))
    val uptimeHours = event.uptimeMillis / (1000 * 60 * 60)
    val freeRamMb = event.ramAvailableBytes / (1024 * 1024)

    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(dateString, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Available RAM: ${freeRamMb} MB", style = MaterialTheme.typography.bodyMedium)
            Text("Uptime: ${uptimeHours} hours", style = MaterialTheme.typography.bodyMedium)
            if (event.ramLowMemory) {
                Text("?? Low Memory condition active", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
