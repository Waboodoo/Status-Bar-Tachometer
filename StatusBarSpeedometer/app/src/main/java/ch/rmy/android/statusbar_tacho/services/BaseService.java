package ch.rmy.android.statusbar_tacho.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import ch.rmy.android.statusbar_tacho.utils.Destroyer;

public abstract class BaseService extends Service {

    Destroyer destroyer = new Destroyer();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Context getContext() {
        return this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyer.destroy();
    }

}
