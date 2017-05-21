package ch.rmy.android.statusbar_tacho.units;

import ch.rmy.android.statusbar_tacho.R;

public class FeetPerSecondUnit extends Unit {

    @Override
    public int getNameRes() {
        return R.string.unit_fts;
    }

    @Override
    public float convertSpeed(float metersPerSecond) {
        return metersPerSecond * 3.28084f;
    }
}
