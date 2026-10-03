package com.amietppawar.numerology.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.amietppawar.numerology.core.ChaldeanNumerology
import com.amietppawar.numerology.data.local.NumerologyProfile
import com.amietppawar.numerology.data.repository.ProfileRepository
import com.amietppawar.numerology.ui.components.BodyText
import com.amietppawar.numerology.ui.components.CosmosButton
import com.amietppawar.numerology.ui.components.CosmosField
import com.amietppawar.numerology.ui.components.CosmosScreen
import com.amietppawar.numerology.ui.components.DateFields
import com.amietppawar.numerology.ui.components.GlassCard
import com.amietppawar.numerology.ui.components.MutedText
import com.amietppawar.numerology.ui.components.SectionTitle
import com.amietppawar.numerology.ui.utils.Dates
import kotlinx.coroutines.launch

/**
 * Saved profiles: add, view, edit and delete. Stored only on this device.
 */
@Composable
fun SavedProfilesScreen(profileRepository: ProfileRepository) {
    val scheme = MaterialTheme.colorScheme
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val profilesState = remember { profileRepository.getAllProfiles() }
        .collectAsState(initial = emptyList())
    val profiles = profilesState.value

    var editingId by rememberSaveable { mutableStateOf(0) }
    var name by rememberSaveable { mutableStateOf("") }
    var day by rememberSaveable { mutableStateOf("") }
    var month by rememberSaveable { mutableStateOf("") }
    var year by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }
    var pendingDelete by remember { mutableStateOf<NumerologyProfile?>(null) }

    fun clearForm() {
        editingId = 0
        name = ""
        day = ""
        month = ""
        year = ""
    }

    CosmosScreen(
        title = "Saved Profiles",
        subtitle = "Keep the numbers of family and friends. They never leave this phone."
    ) {
        GlassCard(accent = scheme.tertiary) {
            Text(
                text = if (editingId == 0) "Add a profile" else "Edit profile",
                style = MaterialTheme.typography.titleMedium,
                color = scheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            CosmosField(
                value = name,
                onValueChange = { name = it },
                label = "Full name",
                capitalization = KeyboardCapitalization.Words
            )
            Spacer(modifier = Modifier.height(12.dp))
            MutedText(text = "Date of birth (optional)")
            Spacer(modifier = Modifier.height(6.dp))
            DateFields(
                day = day,
                month = month,
                year = year,
                onDayChange = { day = it },
                onMonthChange = { month = it },
                onYearChange = { year = it }
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CosmosButton(
                    text = if (editingId == 0) "Save" else "Update",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val cleanName = name.trim()
                        val nameNumber = ChaldeanNumerology.calculateName(cleanName)
                        val hasDate = day.isNotBlank() || month.isNotBlank() || year.isNotBlank()
                        val date = Dates.parse(day, month, year)
                        if (nameNumber.isEmpty()) {
                            message = "Please enter a name with at least one letter."
                        } else if (hasDate && date == null) {
                            message = "Please enter a real date, or leave all three date boxes empty."
                        } else {
                            val birth = if (date != null) ChaldeanNumerology.calculateBirthNumber(date.day) else null
                            val destiny = if (date != null) {
                                ChaldeanNumerology.calculateDestinyNumber(date.year, date.month, date.day)
                            } else {
                                null
                            }
                            val profile = NumerologyProfile(
                                id = editingId,
                                name = cleanName,
                                birthDate = if (date != null) Dates.toStorage(date) else null,
                                nameNumber = nameNumber.reduced,
                                nameNumberCompound = nameNumber.compound,
                                birthNumber = birth?.reduced,
                                birthNumberCompound = birth?.compound,
                                destinyNumber = destiny?.reduced,
                                destinyNumberCompound = destiny?.compound
                            )
                            val updating = editingId != 0
                            scope.launch {
                                if (updating) {
                                    profileRepository.updateProfile(profile)
                                } else {
                                    profileRepository.saveProfile(profile)
                                }
                            }
                            message = if (updating) "Profile updated." else "Profile saved."
                            clearForm()
                            focusManager.clearFocus()
                        }
                    }
                )
                if (editingId != 0) {
                    CosmosButton(
                        text = "Cancel",
                        primary = false,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            clearForm()
                            message = ""
                        }
                    )
                }
            }
            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.secondary
                )
            }
        }

        SectionTitle(text = "Your profiles")
        if (profiles.isEmpty()) {
            MutedText(text = "No profiles yet. Add one above, or save a result from Name Check.")
        }

        profiles.forEach { profile ->
            GlassCard(accent = scheme.secondary) {
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = scheme.onSurface
                )
                val storedDate = Dates.fromStorage(profile.birthDate)
                if (storedDate != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    MutedText(text = "Born " + Dates.display(storedDate))
                }
                Spacer(modifier = Modifier.height(10.dp))
                BodyText(
                    text = "Name number: " + profile.nameNumberCompound + " (" + profile.nameNumber + ")"
                )
                if (profile.birthNumber != null && profile.birthNumberCompound != null) {
                    BodyText(
                        text = "Birth number: " + profile.birthNumberCompound + " (" + profile.birthNumber + ")"
                    )
                }
                if (profile.destinyNumber != null && profile.destinyNumberCompound != null) {
                    BodyText(
                        text = "Destiny number: " + profile.destinyNumberCompound + " (" + profile.destinyNumber + ")"
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CosmosButton(
                        text = "Edit",
                        primary = false,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            editingId = profile.id
                            name = profile.name
                            day = storedDate?.day?.toString() ?: ""
                            month = storedDate?.month?.toString() ?: ""
                            year = storedDate?.year?.toString() ?: ""
                            message = "Editing " + profile.name + ". Change the details above."
                        }
                    )
                    CosmosButton(
                        text = "Delete",
                        primary = false,
                        modifier = Modifier.weight(1f),
                        onClick = { pendingDelete = profile }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    val toDelete = pendingDelete
    if (toDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(text = "Delete this profile?") },
            text = { Text(text = toDelete.name + " will be removed from this phone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch { profileRepository.deleteProfile(toDelete) }
                        if (editingId == toDelete.id) {
                            clearForm()
                        }
                        pendingDelete = null
                    }
                ) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text(text = "Keep")
                }
            }
        )
    }
}
