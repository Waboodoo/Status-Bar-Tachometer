package ch.rmy.android.statusbar_tacho.units;

import android.support.annotation.StringRes;

public abstract class Unit {

    @StringRes
    public abstract int getNameRes();

    public abstract float convertSpeed(float metersPerSecond);

}
