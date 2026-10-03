package com.amietppawar.numerology.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

/** Small helpers for handing a task to another app. Each returns false on failure. */
object Actions {

    fun openWhatsApp(context: Context, number: String, message: String): Boolean {
        val digits = number.filter { it.isDigit() }
        val uri = Uri.parse("https://wa.me/" + digits + "?text=" + Uri.encode(message))
        return start(context, Intent(Intent.ACTION_VIEW, uri))
    }

    fun openEmail(context: Context, address: String, subject: String, body: String): Boolean {
        val uri = Uri.parse(
            "mailto:" + address + "?subject=" + Uri.encode(subject) + "&body=" + Uri.encode(body)
        )
        return start(context, Intent(Intent.ACTION_SENDTO, uri))
    }

    fun openLink(context: Context, url: String): Boolean {
        return start(context, Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    private fun start(context: Context, intent: Intent): Boolean {
        return try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }
}
