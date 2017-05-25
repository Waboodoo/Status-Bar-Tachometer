package ch.rmy.android.statusbar_tacho.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.support.annotation.DrawableRes

import ch.rmy.android.statusbar_tacho.R
import ch.rmy.android.statusbar_tacho.activities.SettingsActivity

class NotificationProvider(context: Context) {

    private val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val builder: Notification.Builder

    init {

        val intent = Intent(context, SettingsActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder = Notification.Builder(context)
                .setSmallIcon(R.drawable.icon_unknown)
                .setContentTitle(context.getString(R.string.current_speed))
                .setContentText(context.getString(R.string.unknown))
                .setContentIntent(pendingIntent)
    }

    fun initializeNotification(service: Service) {
        service.startForeground(NOTIFICATION_ID, builder.build())
    }

    fun updateNotification(message: String, @DrawableRes smallIcon: Int) {
        val notification = builder
                .setContentText(message)
                .setSmallIcon(smallIcon)
                .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {

        private val NOTIFICATION_ID = 1
    }

}
