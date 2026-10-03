package com.devicehealth.assistant.ui.recommendations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devicehealth.assistant.domain.Recommendation
import com.devicehealth.assistant.domain.Severity

@Composable
fun RecommendationsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecommendationsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = onBack) { Text("Back") }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Recommendations", style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(modifier = Modifier.height(16.dp))

        when (state) {
            RecommendationsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is RecommendationsUiState.Error -> {
                Text("Error: ${(state as RecommendationsUiState.Error).throwable.message}")
            }
            is RecommendationsUiState.Success -> {
                val recs = (state as RecommendationsUiState.Success).recommendations
                if (recs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Your device is healthy. No recommendations at this time.")
                    }
                } else {
                    LazyColumn {
                        items(recs) { rec ->
                            RecommendationCard(rec)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationCard(rec: Recommendation) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = when(rec.severity) {
                Severity.CRITICAL -> MaterialTheme.colorScheme.errorContainer
                Severity.WARNING -> MaterialTheme.colorScheme.surfaceVariant
                Severity.INFO -> MaterialTheme.colorScheme.surface
            }
        ),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(rec.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(rec.message, style = MaterialTheme.typography.bodyMedium)
            
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { expanded = !expanded }) {
                Text("Why am I seeing this?")
            }

            if (expanded) {
                Text(rec.explanation, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}
