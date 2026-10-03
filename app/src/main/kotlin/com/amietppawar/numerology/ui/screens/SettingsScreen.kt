package com.amietppawar.numerology.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.amietppawar.numerology.AppConfig
import com.amietppawar.numerology.data.preferences.SettingsRepository
import com.amietppawar.numerology.data.repository.ProfileRepository
import com.amietppawar.numerology.ui.components.BodyText
import com.amietppawar.numerology.ui.components.ChoiceRow
import com.amietppawar.numerology.ui.components.CosmosButton
import com.amietppawar.numerology.ui.components.CosmosField
import com.amietppawar.numerology.ui.components.CosmosScreen
import com.amietppawar.numerology.ui.components.GlassCard
import com.amietppawar.numerology.ui.components.MutedText
import com.amietppawar.numerology.ui.components.SectionTitle
import com.amietppawar.numerology.workers.NotificationScheduler
import kotlinx.coroutines.launch

private val themeModes = listOf("dark", "light", "system")

private fun hourOf(time: String): Int {
    return time.substringBefore(":").toIntOrNull()?.coerceIn(0, 23) ?: 8
}

private fun minuteOf(time: String): Int {
    return time.substringAfter(":", "0").toIntOrNull()?.coerceIn(0, 59) ?: 0
}

/**
 * Settings: appearance, motion, the optional daily notification, privacy,
 * disclaimer, about, and deleting all data.
 */
@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository,
    notificationScheduler: NotificationScheduler,
    profileRepository: ProfileRepository
) {
    val scheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val themeMode = remember { settingsRepository.getThemeMode() }.collectAsState(initial = "dark")
    val reduceMotion = remember { settingsRepository.getReduceMotion() }.collectAsState(initial = false)
    val notificationsOn = remember { settingsRepository.getNotificationsEnabled() }.collectAsState(initial = false)
    val notificationTime = remember { settingsRepository.getNotificationTime() }.collectAsState(initial = "08:00")

    var hourText by rememberSaveable { mutableStateOf("") }
    var minuteText by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }
    var confirmDelete by remember { mutableStateOf(false) }

    // Show the saved time in the two boxes
    LaunchedEffect(notificationTime.value) {
        hourText = hourOf(notificationTime.value).toString()
        minuteText = minuteOf(notificationTime.value).toString().padStart(2, '0')
    }

    fun turnOn() {
        val time = notificationTime.value
        notificationScheduler.scheduleDailyNotification(hourOf(time), minuteOf(time))
        scope.launch { settingsRepository.setNotificationsEnabled(true) }
        message = "Daily number notification is on."
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            turnOn()
        } else {
            message = "Notifications are blocked for this app. You can allow them in your phone's settings."
        }
    }

    CosmosScreen(title = "Settings") {

        SectionTitle(text = "Appearance")
        ChoiceRow(
            options = listOf("Dark", "Light", "System"),
            selectedIndex = themeModes.indexOf(themeMode.value).coerceAtLeast(0),
            onSelect = { index ->
                scope.launch { settingsRepository.setThemeMode(themeModes[index]) }
            }
        )

        Spacer(modifier = Modifier.height(14.dp))
        SettingSwitch(
            title = "Reduce motion",
            note = "Show results instantly, without animation.",
            checked = reduceMotion.value,
            onChange = { value ->
                scope.launch { settingsRepository.setReduceMotion(value) }
            }
        )

        SectionTitle(text = "Daily number")
        SettingSwitch(
            title = "Daily notification",
            note = "One short number insight each day. Off unless you switch it on.",
            checked = notificationsOn.value,
            onChange = { value ->
                if (!value) {
                    notificationScheduler.cancelDailyNotification()
                    scope.launch { settingsRepository.setNotificationsEnabled(false) }
                    message = "Daily number notification is off."
                } else {
                    val needsPermission = Build.VERSION.SDK_INT >= 33 &&
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    if (needsPermission) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        turnOn()
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            CosmosField(
                value = hourText,
                onValueChange = { text -> hourText = text.filter { it.isDigit() }.take(2) },
                label = "Hour (0 to 23)",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
            CosmosField(
                value = minuteText,
                onValueChange = { text -> minuteText = text.filter { it.isDigit() }.take(2) },
                label = "Minute",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
            CosmosButton(
                text = "Set time",
                primary = false,
                modifier = Modifier.weight(1f),
                onClick = {
                    val hour = hourText.toIntOrNull()
                    val minute = minuteText.toIntOrNull()
                    if (hour == null || minute == null || hour > 23 || minute > 59) {
                        message = "Please enter an hour from 0 to 23 and a minute from 0 to 59."
                    } else {
                        val time = hour.toString().padStart(2, '0') + ":" + minute.toString().padStart(2, '0')
                        scope.launch { settingsRepository.setNotificationTime(time) }
                        if (notificationsOn.value) {
                            notificationScheduler.scheduleDailyNotification(hour, minute)
                        }
                        message = "Notification time set to " + time + "."
                    }
                }
            )
        }
        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.secondary
            )
        }

        SectionTitle(text = "Privacy")
        GlassCard(accent = scheme.tertiary) {
            BodyText(text = AppConfig.PRIVACY_POLICY_SUMMARY)
        }

        SectionTitle(text = "Disclaimer")
        GlassCard(accent = scheme.outline) {
            BodyText(text = AppConfig.DISCLAIMER)
        }

        SectionTitle(text = "About")
        GlassCard(accent = scheme.primary) {
            Text(
                text = AppConfig.PRACTICE_NAME,
                style = MaterialTheme.typography.titleMedium,
                color = scheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            BodyText(text = AppConfig.CONSULTANT_BIO)
            Spacer(modifier = Modifier.height(6.dp))
            MutedText(text = "Version 1.0.0  ·  Chaldean system")
        }

        SectionTitle(text = "Your data")
        MutedText(text = "Removes every saved profile and resets all settings on this phone.")
        Spacer(modifier = Modifier.height(10.dp))
        CosmosButton(
            text = "Delete all my data",
            primary = false,
            modifier = Modifier.fillMaxWidth(),
            onClick = { confirmDelete = true }
        )
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(text = "Delete all your data?") },
            text = { Text(text = "All saved profiles and settings will be removed from this phone. This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmDelete = false
                        notificationScheduler.cancelDailyNotification()
                        scope.launch {
                            profileRepository.deleteAllProfiles()
                            settingsRepository.resetAllSettings()
                        }
                    }
                ) {
                    Text(text = "Delete everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingSwitch(
    title: String,
    note: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = scheme.onBackground
            )
            MutedText(text = note)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            modifier = Modifier.semantics { contentDescription = title }
        )
    }
}
