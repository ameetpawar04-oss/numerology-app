package com.amietppawar.numerology.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.amietppawar.numerology.AppConfig
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
import com.amietppawar.numerology.ui.components.NumberPair
import com.amietppawar.numerology.ui.components.SectionTitle
import com.amietppawar.numerology.ui.utils.Dates
import kotlinx.coroutines.launch

/**
 * Birth number (the day reduced) and destiny number (every digit of the date
 * added together) from a date of birth.
 */
@Composable
fun BirthDestinyScreen(
    profileRepository: ProfileRepository,
    onBook: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    var day by rememberSaveable { mutableStateOf("") }
    var month by rememberSaveable { mutableStateOf("") }
    var year by rememberSaveable { mutableStateOf("") }
    var shownDay by rememberSaveable { mutableStateOf(0) }
    var shownMonth by rememberSaveable { mutableStateOf(0) }
    var shownYear by rememberSaveable { mutableStateOf(0) }
    var profileName by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }

    CosmosScreen(
        title = "Birth and Destiny",
        subtitle = "Enter a date of birth as numbers, for example 15, 8, 1990."
    ) {
        DateFields(
            day = day,
            month = month,
            year = year,
            onDayChange = { day = it },
            onMonthChange = { month = it },
            onYearChange = { year = it }
        )
        Spacer(modifier = Modifier.height(14.dp))
        CosmosButton(
            text = "Calculate",
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val date = Dates.parse(day, month, year)
                if (date == null) {
                    message = "Please enter a real date that is not in the future."
                    shownYear = 0
                } else {
                    message = ""
                    shownDay = date.day
                    shownMonth = date.month
                    shownYear = date.year
                    focusManager.clearFocus()
                }
            }
        )
        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.secondary
            )
        }

        if (shownYear > 0) {
            val birth = ChaldeanNumerology.calculateBirthNumber(shownDay)
            val destiny = ChaldeanNumerology.calculateDestinyNumber(shownYear, shownMonth, shownDay)

            Spacer(modifier = Modifier.height(18.dp))
            GlassCard(accent = scheme.tertiary) {
                NumberPair(title = "Birth number", compound = birth.compound, single = birth.reduced)
                Spacer(modifier = Modifier.height(8.dp))
                MutedText(text = "The day of the month, reduced to a single digit.")
                SectionTitle(text = "What " + birth.reduced + " suggests")
                BodyText(text = AppConfig.NUMBER_MEANINGS[birth.reduced] ?: "")
            }

            Spacer(modifier = Modifier.height(14.dp))
            GlassCard(accent = scheme.secondary) {
                NumberPair(title = "Destiny number", compound = destiny.compound, single = destiny.reduced)
                Spacer(modifier = Modifier.height(8.dp))
                MutedText(text = "Every digit of the full date added together.")
                SectionTitle(text = "What " + destiny.reduced + " suggests")
                BodyText(text = AppConfig.NUMBER_MEANINGS[destiny.reduced] ?: "")
            }

            SectionTitle(text = "Save as a profile")
            CosmosField(
                value = profileName,
                onValueChange = { profileName = it },
                label = "Name for this profile",
                capitalization = KeyboardCapitalization.Words
            )
            Spacer(modifier = Modifier.height(12.dp))
            CosmosButton(
                text = "Save profile",
                primary = false,
                enabled = profileName.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val name = profileName.trim()
                    val nameNumber = ChaldeanNumerology.calculateName(name)
                    val date = Dates.parse(shownDay.toString(), shownMonth.toString(), shownYear.toString())
                    if (nameNumber.isEmpty() || date == null) {
                        message = "Please enter a name with at least one letter."
                    } else {
                        scope.launch {
                            profileRepository.saveProfile(
                                NumerologyProfile(
                                    name = name,
                                    birthDate = Dates.toStorage(date),
                                    nameNumber = nameNumber.reduced,
                                    nameNumberCompound = nameNumber.compound,
                                    birthNumber = birth.reduced,
                                    birthNumberCompound = birth.compound,
                                    destinyNumber = destiny.reduced,
                                    destinyNumberCompound = destiny.compound
                                )
                            )
                            message = "Saved to your profiles."
                            profileName = ""
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(18.dp))
            CosmosButton(
                text = "Book a consultation",
                onClick = onBook,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        MutedText(text = AppConfig.DISCLAIMER)
    }
}
