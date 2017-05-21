package ch.rmy.android.statusbar_tacho.units;

import ch.rmy.android.statusbar_tacho.R;

public class MetersPerSecondUnit extends Unit {

    @Override
    public int getNameRes() {
        return R.string.unit_ms;
    }

    @Override
    public float convertSpeed(float metersPerSecond) {
        return metersPerSecond;
    }

}
