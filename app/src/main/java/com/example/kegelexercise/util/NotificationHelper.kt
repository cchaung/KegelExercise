package com.example.kegelexercise.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.kegelexercise.MainActivity
import com.example.kegelexercise.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_TIMER = "kegel_timer"
        const val CHANNEL_COMPLETION = "kegel_completion"
        const val NOTIF_TIMER_ID = 1001
        const val NOTIF_COMPLETION_ID = 1002

        fun createChannels(context: Context) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            NotificationChannel(
                CHANNEL_TIMER,
                "Exercise Timer",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows progress during exercise session"
                manager.createNotificationChannel(this)
            }

            NotificationChannel(
                CHANNEL_COMPLETION,
                "Exercise Complete",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when exercise session is done"
                manager.createNotificationChannel(this)
            }
        }
    }

    private fun buildContentIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun buildTimerNotification(contentText: String): Notification {
        return NotificationCompat.Builder(context, CHANNEL_TIMER)
            .setContentTitle(context.getString(R.string.notification_timer_title))
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(buildContentIntent())
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    fun updateTimerNotification(timeRemaining: String) {
        val notification = buildTimerNotification(
            context.getString(R.string.notification_timer_text, timeRemaining)
        )
        notificationManager.notify(NOTIF_TIMER_ID, notification)
    }

    fun showCompletionNotification() {
        val notification = NotificationCompat.Builder(context, CHANNEL_COMPLETION)
            .setContentTitle(context.getString(R.string.notification_complete_title))
            .setContentText(context.getString(R.string.notification_complete_text))
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(buildContentIntent())
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        notificationManager.notify(NOTIF_COMPLETION_ID, notification)
    }
}
