package ch.rmy.android.statusbar_tacho.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.PowerManager;

public class ScreenStateWatcher implements Destroyable {

    private final Context context;
    private boolean screenOn;
    private final BroadcastReceiver screenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                screenOn = true;
            } else {
                screenOn = false;
            }
            screenStateSource.notify(screenOn);
        }
    };
    private final EventSource<Boolean> screenStateSource = new EventSource<>();

    public ScreenStateWatcher(Context context) {
        this.context = context;
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            screenOn = powerManager.isInteractive();
        } else {
            screenOn = powerManager.isScreenOn();
        }
        register();
    }

    private void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        context.registerReceiver(screenStateReceiver, filter);
    }

    public boolean isScreenOn() {
        return screenOn;
    }

    public EventSource<Boolean> getScreenStateSource() {
        return screenStateSource;
    }

    @Override
    public void destroy() {
        context.unregisterReceiver(screenStateReceiver);
    }

}
