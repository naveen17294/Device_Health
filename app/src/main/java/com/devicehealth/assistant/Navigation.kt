package com.devicehealth.assistant

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.devicehealth.assistant.ui.main.MainScreen

@Composable
fun MainNavigation() {
  val backStack = rememberNavBackStack(Main)

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
      entryProvider =
      entryProvider {
        entry<Main> {
          MainScreen(onItemClick = { navKey -> backStack.add(navKey) }, modifier = Modifier.safeDrawingPadding().padding(16.dp))
        }
        entry<AppAnalysis> {
          com.devicehealth.assistant.ui.analysis.AppAnalysisScreen(
              onBack = { backStack.removeLastOrNull() },
              modifier = Modifier.safeDrawingPadding()
          )
        }
        entry<LagHistory> {
          com.devicehealth.assistant.ui.history.LagHistoryScreen(
              onBack = { backStack.removeLastOrNull() },
              modifier = Modifier.safeDrawingPadding()
          )
        }
        entry<Recommendations> {
          com.devicehealth.assistant.ui.recommendations.RecommendationsScreen(
              onBack = { backStack.removeLastOrNull() },
              modifier = Modifier.safeDrawingPadding()
          )
        }
        entry<Settings> {
          com.devicehealth.assistant.ui.settings.SettingsScreen(
              onBack = { backStack.removeLastOrNull() },
              userSettingsRepository = com.devicehealth.assistant.data.UserSettingsRepository(androidx.compose.ui.platform.LocalContext.current),
              modifier = Modifier.safeDrawingPadding()
          )
        }
      },
  )
}
