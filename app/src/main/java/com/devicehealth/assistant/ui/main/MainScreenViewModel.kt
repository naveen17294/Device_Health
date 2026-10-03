package com.devicehealth.assistant.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devicehealth.assistant.data.DeviceHealthRepository
import com.devicehealth.assistant.data.DeviceHealthState
import com.devicehealth.assistant.data.UserSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DeviceHealthRepository(application)
    private val settingsRepository = UserSettingsRepository(application)
    
    private val _uiState = MutableStateFlow<DeviceHealthUiState>(DeviceHealthUiState.Loading)
    val uiState: StateFlow<DeviceHealthUiState> = _uiState.asStateFlow()

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _uiState.value = DeviceHealthUiState.Loading
            try {
                settingsRepository.lowStorageWarningFlow.collect { warningPercent ->
                    val state = repository.getHealthSnapshot()
                    _uiState.value = DeviceHealthUiState.Success(state, warningPercent)
                }
            } catch (e: Exception) {
                _uiState.value = DeviceHealthUiState.Error(e)
            }
        }
    }
}

sealed interface DeviceHealthUiState {
    object Loading : DeviceHealthUiState
    data class Error(val throwable: Throwable) : DeviceHealthUiState
    data class Success(val data: DeviceHealthState, val warningPercent: Int) : DeviceHealthUiState
}
