package ch.rmy.android.statusbar_tacho.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi

import ch.rmy.android.statusbar_tacho.R
import ch.rmy.android.statusbar_tacho.activities.SettingsActivity

class NotificationProvider(context: Context) {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val builder: Notification.Builder

    init {
        val intent = Intent(context, SettingsActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_IMMUTABLE
                } else {
                    0
                }
            )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(createChannel(context))
        }

        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(context)
        }
            .setSmallIcon(R.drawable.icon_unknown)
            .setContentTitle(context.getString(R.string.current_speed))
            .setContentText(context.getString(R.string.unknown))
            .setContentIntent(pendingIntent)
            .let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    it.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
                } else {
                    it
                }
            }
    }

    fun initializeNotification(service: Service) {
        service.startForeground(NOTIFICATION_ID, builder.build())
    }

    fun getInitialNotification(): Notification =
        builder.build()

    fun updateNotification(message: String, @DrawableRes smallIcon: Int) {
        val notification = builder
            .setContentText(message)
            .setSmallIcon(smallIcon)
            .setOnlyAlertOnce(true)
            .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {

        const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "notification"

        @RequiresApi(Build.VERSION_CODES.O)
        private fun createChannel(context: Context): NotificationChannel =
            NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel),
                NotificationManager.IMPORTANCE_LOW
            )
                .apply {
                    enableLights(false)
                    enableVibration(false)
                    setShowBadge(false)
                }

    }

}
