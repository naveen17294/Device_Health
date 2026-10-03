package com.devicehealth.assistant.ui.recommendations

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devicehealth.assistant.data.AppDatabase
import com.devicehealth.assistant.data.AppStatsRepository
import com.devicehealth.assistant.data.DeviceHealthRepository
import com.devicehealth.assistant.data.UserSettingsRepository
import com.devicehealth.assistant.domain.Recommendation
import com.devicehealth.assistant.domain.RecommendationEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecommendationsViewModel(application: Application) : AndroidViewModel(application) {
    private val healthRepo = DeviceHealthRepository(application)
    private val appStatsRepo = AppStatsRepository(application)
    private val db = AppDatabase.getDatabase(application)
    private val settings = UserSettingsRepository(application)
    private val engine = RecommendationEngine()

    private val _uiState = MutableStateFlow<RecommendationsUiState>(RecommendationsUiState.Loading)
    val uiState: StateFlow<RecommendationsUiState> = _uiState.asStateFlow()

    init {
        loadRecommendations()
    }

    fun loadRecommendations() {
        _uiState.value = RecommendationsUiState.Loading
        viewModelScope.launch {
            try {
                combine(
                    db.lagDao().getAllEvents(),
                    settings.largeAppThresholdFlow,
                    settings.lowStorageWarningFlow
                ) { lags, threshold, percent ->
                    Triple(lags, threshold, percent)
                }.collect { (lags, threshold, warningPercent) ->
                    val healthState = withContext(Dispatchers.IO) { healthRepo.getHealthSnapshot() }
                    val apps = if (appStatsRepo.hasUsageAccess()) {
                        withContext(Dispatchers.IO) { appStatsRepo.getAppStats() }
                    } else emptyList()
                    
                    val recs = engine.evaluate(healthState, lags, apps, threshold, warningPercent)
                    _uiState.value = RecommendationsUiState.Success(recs)
                }
            } catch (e: Exception) {
                _uiState.value = RecommendationsUiState.Error(e)
            }
        }
    }
}

sealed interface RecommendationsUiState {
    object Loading : RecommendationsUiState
    data class Error(val throwable: Throwable) : RecommendationsUiState
    data class Success(val recommendations: List<Recommendation>) : RecommendationsUiState
}
