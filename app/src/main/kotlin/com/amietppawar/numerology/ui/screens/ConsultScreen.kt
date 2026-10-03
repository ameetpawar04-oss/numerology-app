package com.amietppawar.numerology.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.amietppawar.numerology.AppConfig
import com.amietppawar.numerology.ui.components.BodyText
import com.amietppawar.numerology.ui.components.ChoiceRow
import com.amietppawar.numerology.ui.components.CosmosButton
import com.amietppawar.numerology.ui.components.CosmosField
import com.amietppawar.numerology.ui.components.CosmosScreen
import com.amietppawar.numerology.ui.components.GlassCard
import com.amietppawar.numerology.ui.components.MutedText
import com.amietppawar.numerology.ui.components.SectionTitle
import com.amietppawar.numerology.ui.utils.Actions

/**
 * Consultations: choose a package, choose online or in person, and send a
 * booking request by WhatsApp or email. Payment is arranged outside the app.
 */
@Composable
fun ConsultScreen() {
    val scheme = MaterialTheme.colorScheme
    val context = LocalContext.current

    var packageIndex by rememberSaveable { mutableStateOf(0) }
    var modeIndex by rememberSaveable { mutableStateOf(0) }
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var preferred by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }

    val whatsApp = AppConfig.WHATSAPP_NUMBER.trim()
    val email = AppConfig.SUPPORT_EMAIL.trim()
    val schedulingLink = AppConfig.SCHEDULING_LINK.trim()
    val paymentLink = AppConfig.PAYMENT_LINK.trim()
    val amazonLink = AppConfig.AMAZON_BOOK_LINK.trim()
    val online = modeIndex == 0
    val chosen = AppConfig.PACKAGES[packageIndex.coerceIn(0, AppConfig.PACKAGES.size - 1)]

    fun requestText(): String {
        val builder = StringBuilder()
        builder.append("Consultation request for ").append(AppConfig.CONSULTANT_NAME).append("\n\n")
        builder.append("Package: ").append(chosen.name).append("\n")
        builder.append("Type: ").append(if (online) "Online (video call)" else "In person, Mumbai").append("\n")
        builder.append("Name: ").append(name.trim()).append("\n")
        builder.append("Phone: ").append(phone.trim()).append("\n")
        if (preferred.isNotBlank()) {
            builder.append("Preferred date and time: ").append(preferred.trim()).append("\n")
        }
        if (note.isNotBlank()) {
            builder.append("Message: ").append(note.trim()).append("\n")
        }
        return builder.toString()
    }

    fun formProblem(): String {
        if (name.trim().length < 2) return "Please enter your name."
        if (phone.count { it.isDigit() } < 8) return "Please enter a phone number with at least 8 digits."
        return ""
    }

    CosmosScreen(
        title = "Consultations",
        subtitle = "A personal reading with " + AppConfig.CONSULTANT_NAME + ", " + AppConfig.CONSULTANT_LOCATION + "."
    ) {
        BodyText(text = AppConfig.CONSULTANT_BIO)

        SectionTitle(text = "1. Choose a package")
        AppConfig.PACKAGES.forEachIndexed { index, item ->
            val selected = index == packageIndex
            GlassCard(
                accent = if (selected) scheme.secondary else scheme.outline,
                modifier = Modifier.clickable(
                    role = Role.RadioButton,
                    onClickLabel = "Choose " + item.name,
                    onClick = { packageIndex = index }
                )
            ) {
                Text(
                    text = item.name + if (selected) "  (selected)" else "",
                    style = MaterialTheme.typography.titleMedium,
                    color = scheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "INR " + formatRupees(item.priceINR) + "  ·  " + item.durationMinutes + " minutes",
                    style = MaterialTheme.typography.titleSmall,
                    color = scheme.secondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                MutedText(text = item.description)
                if (selected) {
                    Spacer(modifier = Modifier.height(8.dp))
                    item.features.forEach { feature ->
                        BodyText(text = "•  " + feature)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        SectionTitle(text = "2. Online or in person")
        ChoiceRow(
            options = listOf("Online", "In person"),
            selectedIndex = modeIndex,
            onSelect = { modeIndex = it }
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (online) {
            GlassCard(accent = scheme.tertiary) {
                Text(
                    text = "Online consultation",
                    style = MaterialTheme.typography.titleMedium,
                    color = scheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                BodyText(text = "1.  Send your request below.")
                BodyText(text = "2.  You receive a confirmed time and a video call link.")
                BodyText(text = "3.  Join from anywhere, on your phone or laptop.")
                Spacer(modifier = Modifier.height(8.dp))
                MutedText(text = "Availability: " + AppConfig.BOOKING_ONLINE_AVAILABILITY)
                MutedText(text = "Keep ready: the exact spelling of the name you use, and your date of birth.")
            }
        } else {
            GlassCard(accent = scheme.secondary) {
                Text(
                    text = "In-person consultation",
                    style = MaterialTheme.typography.titleMedium,
                    color = scheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                BodyText(text = "1.  Send your request below.")
                BodyText(text = "2.  You receive a confirmed time and the address.")
                BodyText(text = "3.  Meet face to face for an unhurried session.")
                Spacer(modifier = Modifier.height(8.dp))
                MutedText(text = "Location: " + AppConfig.BOOKING_INPERSON_LOCATION)
                MutedText(text = "Availability: " + AppConfig.BOOKING_INPERSON_AVAILABILITY)
                MutedText(text = "Bring: the exact spelling of the name you use, and your date of birth.")
            }
        }

        SectionTitle(text = "3. Your details")
        CosmosField(
            value = name,
            onValueChange = { name = it },
            label = "Your name",
            capitalization = KeyboardCapitalization.Words
        )
        Spacer(modifier = Modifier.height(12.dp))
        CosmosField(
            value = phone,
            onValueChange = { phone = it },
            label = "Phone number",
            keyboardType = KeyboardType.Phone
        )
        Spacer(modifier = Modifier.height(12.dp))
        CosmosField(
            value = preferred,
            onValueChange = { preferred = it },
            label = "Preferred date and time (optional)"
        )
        Spacer(modifier = Modifier.height(12.dp))
        CosmosField(
            value = note,
            onValueChange = { note = it },
            label = "Anything you would like to ask (optional)",
            capitalization = KeyboardCapitalization.Sentences,
            singleLine = false
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (whatsApp.isEmpty() && email.isEmpty()) {
            CosmosButton(
                text = "Send booking request",
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val problem = formProblem()
                    message = if (problem.isNotEmpty()) problem else "Booking opens soon. Please check back shortly."
                }
            )
        }
        if (whatsApp.isNotEmpty()) {
            CosmosButton(
                text = "Send request on WhatsApp",
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val problem = formProblem()
                    if (problem.isNotEmpty()) {
                        message = problem
                    } else if (Actions.openWhatsApp(context, whatsApp, requestText())) {
                        message = "WhatsApp opened with your request. Press send there to finish."
                    } else {
                        message = "WhatsApp could not be opened on this phone."
                    }
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        if (email.isNotEmpty()) {
            CosmosButton(
                text = "Send request by email",
                primary = whatsApp.isEmpty(),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val problem = formProblem()
                    if (problem.isNotEmpty()) {
                        message = problem
                    } else if (Actions.openEmail(context, email, "Consultation request: " + chosen.name, requestText())) {
                        message = "Your email app opened with the request. Press send there to finish."
                    } else {
                        message = "No email app was found on this phone."
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
        if (schedulingLink.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            CosmosButton(
                text = "Pick a time online",
                primary = false,
                modifier = Modifier.fillMaxWidth(),
                onClick = { Actions.openLink(context, schedulingLink) }
            )
        }
        if (paymentLink.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            CosmosButton(
                text = "Pay for a confirmed booking",
                primary = false,
                modifier = Modifier.fillMaxWidth(),
                onClick = { Actions.openLink(context, paymentLink) }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        MutedText(text = "Fees are paid directly to the consultant, outside this app.")

        SectionTitle(text = "The book")
        GlassCard(accent = scheme.primary) {
            Text(
                text = AppConfig.BOOK_TITLE,
                style = MaterialTheme.typography.headlineSmall,
                color = scheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            BodyText(text = AppConfig.BOOK_DESCRIPTION)
            Spacer(modifier = Modifier.height(4.dp))
            MutedText(text = AppConfig.BOOK_STATUS)
            if (amazonLink.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                CosmosButton(
                    text = "Buy on Amazon",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { Actions.openLink(context, amazonLink) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        MutedText(text = AppConfig.DISCLAIMER)
    }
}

/** 10000 becomes "10,000". */
private fun formatRupees(amount: Int): String {
    val digits = amount.toString()
    val result = StringBuilder()
    var count = 0
    for (i in digits.length - 1 downTo 0) {
        result.append(digits[i])
        count = count + 1
        if (count % 3 == 0 && i > 0) {
            result.append(',')
        }
    }
    return result.reverse().toString()
}
