package com.amietppawar.numerology.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.amietppawar.numerology.AppConfig
import com.amietppawar.numerology.ui.components.CosmosButton
import com.amietppawar.numerology.ui.components.MutedText
import com.amietppawar.numerology.ui.components.Orrery

private class OnboardingPage(val title: String, val body: String)

private val onboardingPages = listOf(
    OnboardingPage(
        "Your name carries a number",
        "In the Chaldean system every letter has a value from 1 to 8. " +
            "Type any name and watch its letters travel to their numbers."
    ),
    OnboardingPage(
        "See the whole picture",
        "Check a name, compare spellings, and find the birth and destiny numbers " +
            "from a date of birth. Save profiles for family and friends. " +
            "Everything stays on this phone."
    ),
    OnboardingPage(
        "A tool for reflection",
        "When you want a personal reading, you can book a consultation with " +
            AppConfig.CONSULTANT_NAME + ", online or in person."
    )
)

/** Three short introduction screens, shown once. */
@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    var pageIndex by rememberSaveable { mutableStateOf(0) }
    val page = onboardingPages[pageIndex]
    val isLast = pageIndex == onboardingPages.size - 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Orrery(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            color = scheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() }
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = page.body,
            style = MaterialTheme.typography.bodyLarge,
            color = scheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (isLast) {
            Spacer(modifier = Modifier.height(16.dp))
            MutedText(text = AppConfig.DISCLAIMER, centered = true)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            onboardingPages.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .size(if (index == pageIndex) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (index == pageIndex) scheme.secondary else scheme.outline)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        CosmosButton(
            text = if (isLast) "Get started" else "Next",
            onClick = {
                if (isLast) {
                    onComplete()
                } else {
                    pageIndex = pageIndex + 1
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        if (!isLast) {
            Spacer(modifier = Modifier.height(10.dp))
            CosmosButton(
                text = "Skip",
                onClick = onComplete,
                primary = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
