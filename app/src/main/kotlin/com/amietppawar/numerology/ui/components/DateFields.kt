package com.amietppawar.numerology.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/** Day, month and year entered as numbers. Works the same on every phone. */
@Composable
fun DateFields(
    day: String,
    month: String,
    year: String,
    onDayChange: (String) -> Unit,
    onMonthChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CosmosField(
            value = day,
            onValueChange = { text -> onDayChange(text.filter { it.isDigit() }.take(2)) },
            label = "Day",
            keyboardType = KeyboardType.Number,
            modifier = Modifier.weight(1f)
        )
        CosmosField(
            value = month,
            onValueChange = { text -> onMonthChange(text.filter { it.isDigit() }.take(2)) },
            label = "Month",
            keyboardType = KeyboardType.Number,
            modifier = Modifier.weight(1f)
        )
        CosmosField(
            value = year,
            onValueChange = { text -> onYearChange(text.filter { it.isDigit() }.take(4)) },
            label = "Year",
            keyboardType = KeyboardType.Number,
            modifier = Modifier.weight(1.4f)
        )
    }
}
