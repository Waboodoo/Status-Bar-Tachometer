package ch.rmy.android.statusbar_tacho.services;

import android.content.Context;
import android.content.Intent;

import ch.rmy.android.statusbar_tacho.R;
import ch.rmy.android.statusbar_tacho.icons.IconProvider;
import ch.rmy.android.statusbar_tacho.location.SpeedWatcher;
import ch.rmy.android.statusbar_tacho.notifications.NotificationProvider;
import ch.rmy.android.statusbar_tacho.units.Unit;
import ch.rmy.android.statusbar_tacho.utils.Destroyable;
import ch.rmy.android.statusbar_tacho.utils.EventSource;
import ch.rmy.android.statusbar_tacho.utils.ScreenStateWatcher;
import ch.rmy.android.statusbar_tacho.utils.Settings;

public class TachoService extends BaseService {

    private SpeedWatcher speedWatcher;
    private IconProvider iconProvider;
    private NotificationProvider notificationProvider;
    private Unit unit;

    @Override
    public void onCreate() {
        super.onCreate();

        final Settings settings = new Settings(this);
        unit = settings.getUnit();

        iconProvider = new IconProvider(getContext());
        notificationProvider = new NotificationProvider(getContext());
        speedWatcher = destroyer.own(new SpeedWatcher(getContext()));
        ScreenStateWatcher screenStateWatcher = destroyer.own(new ScreenStateWatcher(getContext()));

        notificationProvider.initializeNotification(this);

        speedWatcher.getSpeedSource().bind(new EventSource.Observer<Float>() {
            @Override
            public void on(Float currentSpeed) {
                updateNotification(currentSpeed);
            }
        }, speedWatcher.getCurrentSpeed());

        screenStateWatcher.getScreenStateSource().bind(new EventSource.Observer<Boolean>() {
            @Override
            public void on(Boolean isScreenOn) {
                if (isScreenOn) {
                    speedWatcher.enable();
                } else {
                    speedWatcher.disable();
                }
            }
        }, screenStateWatcher.isScreenOn());

        settings.setRunning(true);
        destroyer.own(new Destroyable() {
            @Override
            public void destroy() {
                settings.setRunning(false);
            }
        });
    }

    private void updateNotification(Float currentSpeed) {
        String message;
        int iconRes;
        if (currentSpeed == null) {
            message = speedWatcher.isGPSEnabled()
                    ? getString(R.string.unknown)
                    : getString(R.string.gps_disabled);
            iconRes = R.drawable.icon_unknown;
        } else {
            message = getString(
                    R.string.speed_format,
                    unit.convertSpeed(currentSpeed),
                    getString(unit.getNameRes())
            );
            iconRes = iconProvider.getIconForNumber(Math.round(currentSpeed));
        }
        notificationProvider.updateNotification(message, iconRes);
    }

    public static void setRunningState(Context context, boolean state) {
        Intent intent = new Intent(context, TachoService.class);
        if (state) {
            context.startService(intent);
        } else {
            context.stopService(intent);
        }
    }

    public static boolean isRunning(Context context) {
        return new Settings(context).isRunning();
    }
}
