package com.amietppawar.numerology.workers

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Schedules and cancels the optional daily number notification.
 */
class NotificationScheduler(private val context: Context) {

    /**
     * Show the notification once a day at about the given time.
     * Android may delay it slightly to save battery.
     */
    fun scheduleDailyNotification(hour: Int, minute: Int) {
        val request = PeriodicWorkRequestBuilder<DailyNumberWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(millisUntil(hour, minute), TimeUnit.MILLISECONDS)
            .build()

        @Suppress("DEPRECATION")
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DailyNumberWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            request
        )
    }

    fun cancelDailyNotification() {
        WorkManager.getInstance(context).cancelUniqueWork(DailyNumberWorker.WORK_NAME)
    }

    private fun millisUntil(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val next = Calendar.getInstance()
        next.set(Calendar.HOUR_OF_DAY, hour)
        next.set(Calendar.MINUTE, minute)
        next.set(Calendar.SECOND, 0)
        next.set(Calendar.MILLISECOND, 0)
        if (!next.after(now)) {
            next.add(Calendar.DAY_OF_MONTH, 1)
        }
        return next.timeInMillis - now.timeInMillis
    }
}
