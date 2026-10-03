package com.devicehealth.assistant.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.CreationExtras
import com.devicehealth.assistant.data.UserSettingsRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {
    
    val largeAppThresholdMb = userSettingsRepository.largeAppThresholdFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 500L * 1024 * 1024)

    val lowStorageWarningPercent = userSettingsRepository.lowStorageWarningFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 15)

    fun updateLargeAppThreshold(mb: Int) {
        viewModelScope.launch {
            userSettingsRepository.setLargeAppThreshold(mb.toLong() * 1024 * 1024)
        }
    }

    fun updateLowStorageWarningPercent(percent: Int) {
        viewModelScope.launch {
            userSettingsRepository.setLowStorageWarningPercent(percent)
        }
    }
}

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    userSettingsRepository: UserSettingsRepository,
    modifier: Modifier = Modifier,
) {
    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(userSettingsRepository) as T
        }
    }
    val viewModel: SettingsViewModel = viewModel(factory = factory)
    
    val largeAppThreshold by viewModel.largeAppThresholdMb.collectAsStateWithLifecycle()
    val lowStoragePercent by viewModel.lowStorageWarningPercent.collectAsStateWithLifecycle()

    val largeAppMb = (largeAppThreshold / (1024 * 1024)).toFloat()

    Column(modifier = modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = onBack) { Text("Back") }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "App Settings", style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(modifier = Modifier.height(32.dp))

        Text("Large App Threshold (MB)", style = MaterialTheme.typography.titleMedium)
        Text("Warn me if an app is larger than ${largeAppMb.toInt()} MB", style = MaterialTheme.typography.bodyMedium)
        Slider(
            value = largeAppMb,
            onValueChange = { viewModel.updateLargeAppThreshold(it.toInt()) },
            valueRange = 100f..2000f,
            steps = 19
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        Text("Low Storage Warning (%)", style = MaterialTheme.typography.titleMedium)
        Text("Warn me if free space drops below ${lowStoragePercent}%", style = MaterialTheme.typography.bodyMedium)
        Slider(
            value = lowStoragePercent.toFloat(),
            onValueChange = { viewModel.updateLowStorageWarningPercent(it.toInt()) },
            valueRange = 5f..50f,
            steps = 45
        )
    }
}
