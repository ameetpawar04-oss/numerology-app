package com.amietppawar.numerology.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amietppawar.numerology.data.preferences.SettingsRepository
import com.amietppawar.numerology.data.repository.ProfileRepository
import com.amietppawar.numerology.ui.components.OrbitalBottomNavigation
import com.amietppawar.numerology.ui.components.goTo
import com.amietppawar.numerology.ui.screens.BirthDestinyScreen
import com.amietppawar.numerology.ui.screens.ConsultScreen
import com.amietppawar.numerology.ui.screens.HomeScreen
import com.amietppawar.numerology.ui.screens.LearnScreen
import com.amietppawar.numerology.ui.screens.NameCheckScreen
import com.amietppawar.numerology.ui.screens.SavedProfilesScreen
import com.amietppawar.numerology.ui.screens.SettingsScreen
import com.amietppawar.numerology.workers.NotificationScheduler

/** Route names. The first five appear in the orbital navigation. */
object Routes {
    const val HOME = "home"
    const val NAME_CHECK = "namecheck"
    const val NUMBERS = "numbers"
    const val LEARN = "learn"
    const val CONSULT = "consult"
    const val PROFILES = "profiles"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavigation(
    profileRepository: ProfileRepository,
    settingsRepository: SettingsRepository,
    notificationScheduler: NotificationScheduler
) {
    val navController = rememberNavController()

    // A name typed on the home screen, waiting to be revealed on Name Check.
    var pendingName by rememberSaveable { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.weight(1f)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onReveal = { name ->
                        pendingName = name
                        navController.goTo(Routes.NAME_CHECK)
                    },
                    onOpen = { route -> navController.goTo(route) }
                )
            }
            composable(Routes.NAME_CHECK) {
                NameCheckScreen(
                    profileRepository = profileRepository,
                    pendingName = pendingName,
                    onPendingConsumed = { pendingName = "" },
                    onBook = { navController.goTo(Routes.CONSULT) }
                )
            }
            composable(Routes.NUMBERS) {
                BirthDestinyScreen(
                    profileRepository = profileRepository,
                    onBook = { navController.goTo(Routes.CONSULT) }
                )
            }
            composable(Routes.LEARN) {
                LearnScreen(onBook = { navController.goTo(Routes.CONSULT) })
            }
            composable(Routes.CONSULT) {
                ConsultScreen()
            }
            composable(Routes.PROFILES) {
                SavedProfilesScreen(profileRepository = profileRepository)
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    settingsRepository = settingsRepository,
                    notificationScheduler = notificationScheduler,
                    profileRepository = profileRepository
                )
            }
        }
        OrbitalBottomNavigation(navController)
    }
}
