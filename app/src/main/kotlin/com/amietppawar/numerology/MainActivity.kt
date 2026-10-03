package com.amietppawar.numerology

import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.amietppawar.numerology.data.preferences.SettingsRepository
import com.amietppawar.numerology.data.repository.ProfileRepository
import com.amietppawar.numerology.ui.navigation.AppNavigation
import com.amietppawar.numerology.ui.screens.OnboardingScreen
import com.amietppawar.numerology.ui.theme.AmietPpawarNumerologyTheme
import com.amietppawar.numerology.ui.theme.LocalReduceMotion
import com.amietppawar.numerology.workers.NotificationScheduler
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val profileRepository = ProfileRepository(AppGraph.database(applicationContext).profileDao())
        val settingsRepository = SettingsRepository(applicationContext)
        val notificationScheduler = NotificationScheduler(applicationContext)

        // Respect the phone's "remove animations" accessibility setting.
        val systemAnimationsOff = Settings.Global.getFloat(
            contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        ) == 0f

        setContent {
            val themeMode = remember { settingsRepository.getThemeMode() }
                .collectAsState(initial = "dark")
            val reduceMotion = remember { settingsRepository.getReduceMotion() }
                .collectAsState(initial = false)
            val firstLaunch = remember { settingsRepository.getFirstLaunch() }
                .collectAsState(initial = null as Boolean?)
            val scope = rememberCoroutineScope()

            AmietPpawarNumerologyTheme(themeMode = themeMode.value) {
                CompositionLocalProvider(
                    LocalReduceMotion provides (reduceMotion.value || systemAnimationsOff)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        val first = firstLaunch.value
                        if (first == null) {
                            // Settings are still loading: show the plain background.
                        } else if (first) {
                            OnboardingScreen(
                                onComplete = {
                                    scope.launch { settingsRepository.setFirstLaunchComplete() }
                                }
                            )
                        } else {
                            AppNavigation(
                                profileRepository = profileRepository,
                                settingsRepository = settingsRepository,
                                notificationScheduler = notificationScheduler
                            )
                        }
                    }
                }
            }
        }
    }
}
