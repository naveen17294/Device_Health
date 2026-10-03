package com.devicehealth.assistant

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Main : NavKey
@Serializable data object AppAnalysis : NavKey
@Serializable data object LagHistory : NavKey
@Serializable data object Recommendations : NavKey
@Serializable data object Settings : NavKey
