package ch.rmy.android.statusbar_tacho.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;

import ch.rmy.android.statusbar_tacho.R;
import ch.rmy.android.statusbar_tacho.activities.SettingsActivity;

public class NotificationProvider {

    private static final int NOTIFICATION_ID = 1;

    private final NotificationManager notificationManager;

    private final Notification.Builder builder;

    public NotificationProvider(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, SettingsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.icon_unknown)
                .setContentTitle(context.getString(R.string.current_speed))
                .setContentText(context.getString(R.string.unknown))
                .setContentIntent(pendingIntent);
    }

    public void initializeNotification(Service service) {
        service.startForeground(NOTIFICATION_ID, builder.build());
    }

    public void updateNotification(String message, @DrawableRes int smallIcon) {
        Notification notification = builder
                .setContentText(message)
                .setSmallIcon(smallIcon)
                .build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

}
