package com.amietppawar.numerology.ui.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.amietppawar.numerology.AppConfig
import com.amietppawar.numerology.ui.components.BodyText
import com.amietppawar.numerology.ui.components.CosmosButton
import com.amietppawar.numerology.ui.components.CosmosScreen
import com.amietppawar.numerology.ui.components.GlassCard
import com.amietppawar.numerology.ui.components.MutedText
import org.json.JSONObject

private class Article(val title: String, val minutes: Int, val body: String)

/** Reads the articles from assets/articles.json. Edit that file to change them. */
private fun loadArticles(context: Context): List<Article> {
    return try {
        val text = context.assets.open("articles.json").bufferedReader().use { it.readText() }
        val array = JSONObject(text).getJSONArray("articles")
        val list = ArrayList<Article>()
        for (i in 0 until array.length()) {
            val item = array.getJSONObject(i)
            list.add(
                Article(
                    title = item.getString("title"),
                    minutes = item.optInt("minutes", 2),
                    body = item.getString("body")
                )
            )
        }
        list
    } catch (e: Exception) {
        emptyList()
    }
}

/** Short beginner articles. Tap one to open or close it. */
@Composable
fun LearnScreen(onBook: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val articles = remember { loadArticles(context) }
    var openIndex by rememberSaveable { mutableStateOf(-1) }

    CosmosScreen(
        title = "Learn",
        subtitle = "Short reads on where numerology comes from and how it works."
    ) {
        if (articles.isEmpty()) {
            MutedText(text = "The articles could not be loaded.")
        }

        articles.forEachIndexed { index, article ->
            val open = index == openIndex
            GlassCard(
                accent = if (open) scheme.secondary else scheme.primary,
                modifier = Modifier.clickable(
                    role = Role.Button,
                    onClickLabel = if (open) "Close article" else "Open article",
                    onClick = { openIndex = if (open) -1 else index }
                )
            ) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = scheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                MutedText(
                    text = article.minutes.toString() + " min read" +
                        if (open) "" else "  ·  tap to read"
                )
                if (open) {
                    Spacer(modifier = Modifier.height(12.dp))
                    BodyText(text = article.body)
                    Spacer(modifier = Modifier.height(14.dp))
                    CosmosButton(
                        text = "Book a consultation",
                        onClick = onBook,
                        primary = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))
        MutedText(text = AppConfig.DISCLAIMER)
    }
}
