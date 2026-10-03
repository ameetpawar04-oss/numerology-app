package com.amietppawar.numerology.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.amietppawar.numerology.AppConfig
import com.amietppawar.numerology.DailyNumber
import com.amietppawar.numerology.ui.components.CosmosButton
import com.amietppawar.numerology.ui.components.CosmosField
import com.amietppawar.numerology.ui.components.GlassCard
import com.amietppawar.numerology.ui.components.MutedText
import com.amietppawar.numerology.ui.components.Orrery
import com.amietppawar.numerology.ui.navigation.Routes

/**
 * Home: the Orrery, a quick name field, the number of the day and shortcuts.
 */
@Composable
fun HomeScreen(
    onReveal: (String) -> Unit,
    onOpen: (String) -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    var name by rememberSaveable { mutableStateOf("") }
    val dailyNumber = remember { DailyNumber.today() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Orrery(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        Text(
            text = "Clarity through numbers",
            style = MaterialTheme.typography.headlineMedium,
            color = scheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() }
        )
        Spacer(modifier = Modifier.height(6.dp))
        MutedText(
            text = "Chaldean numerology with " + AppConfig.CONSULTANT_NAME,
            centered = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        GlassCard(accent = scheme.tertiary) {
            CosmosField(
                value = name,
                onValueChange = { name = it },
                label = "Type a name to reveal its number",
                capitalization = KeyboardCapitalization.Words
            )
            Spacer(modifier = Modifier.height(12.dp))
            CosmosButton(
                text = "Reveal",
                onClick = { onReveal(name.trim()) },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        GlassCard(accent = scheme.secondary) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = dailyNumber.toString(),
                    style = MaterialTheme.typography.displayMedium,
                    color = scheme.secondary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Number of the day",
                        style = MaterialTheme.typography.titleSmall,
                        color = scheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    MutedText(text = DailyNumber.meaning(dailyNumber))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Shortcut(
                title = "Birth and destiny",
                note = "From a date of birth",
                modifier = Modifier.weight(1f),
                onClick = { onOpen(Routes.NUMBERS) }
            )
            Shortcut(
                title = "Saved profiles",
                note = "Family and friends",
                modifier = Modifier.weight(1f),
                onClick = { onOpen(Routes.PROFILES) }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Shortcut(
                title = "Book a consultation",
                note = "Online or in person",
                modifier = Modifier.weight(1f),
                onClick = { onOpen(Routes.CONSULT) }
            )
            Shortcut(
                title = "Settings",
                note = "Theme, privacy, data",
                modifier = Modifier.weight(1f),
                onClick = { onOpen(Routes.SETTINGS) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        MutedText(text = AppConfig.DISCLAIMER, centered = true)
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun Shortcut(
    title: String,
    note: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(scheme.surfaceVariant)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(14.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = scheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        MutedText(text = note)
    }
}
