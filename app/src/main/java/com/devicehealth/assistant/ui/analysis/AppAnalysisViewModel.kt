package com.devicehealth.assistant.ui.analysis

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devicehealth.assistant.data.AppInfo
import com.devicehealth.assistant.data.AppStatsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppAnalysisViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppStatsRepository(application)
    
    private val _uiState = MutableStateFlow<AppAnalysisUiState>(AppAnalysisUiState.Loading)
    val uiState: StateFlow<AppAnalysisUiState> = _uiState.asStateFlow()

    init {
        checkPermissionAndLoad()
    }

    fun checkPermissionAndLoad() {
        if (!repository.hasUsageAccess()) {
            _uiState.value = AppAnalysisUiState.NeedsPermission
            return
        }

        _uiState.value = AppAnalysisUiState.Loading
        viewModelScope.launch {
            try {
                val stats = withContext(Dispatchers.IO) { repository.getAppStats() }
                _uiState.value = AppAnalysisUiState.Success(stats)
            } catch (e: Exception) {
                _uiState.value = AppAnalysisUiState.Error(e)
            }
        }
    }
}

sealed interface AppAnalysisUiState {
    object Loading : AppAnalysisUiState
    object NeedsPermission : AppAnalysisUiState
    data class Error(val throwable: Throwable) : AppAnalysisUiState
    data class Success(val apps: List<AppInfo>) : AppAnalysisUiState
}
