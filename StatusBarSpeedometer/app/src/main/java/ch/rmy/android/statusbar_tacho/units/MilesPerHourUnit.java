package ch.rmy.android.statusbar_tacho.units;

import ch.rmy.android.statusbar_tacho.R;

public class MilesPerHourUnit extends Unit {

    @Override
    public int getNameRes() {
        return R.string.unit_mph;
    }

    @Override
    public float convertSpeed(float metersPerSecond) {
        return metersPerSecond * 2.23694f;
    }

}
