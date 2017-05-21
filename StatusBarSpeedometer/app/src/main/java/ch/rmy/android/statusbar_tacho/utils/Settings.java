package ch.rmy.android.statusbar_tacho.utils;

import android.content.Context;
import android.content.SharedPreferences;

import ch.rmy.android.statusbar_tacho.units.Unit;
import ch.rmy.android.statusbar_tacho.units.Units;

public class Settings {

    private static final String PREF = "pref";
    private static final String PREF_SERVICE = "service";
    private static final String PREF_UNIT = "unit";

    private final SharedPreferences preferences;

    public Settings(Context context) {
        preferences = context.getSharedPreferences(PREF, 0);
    }

    public boolean isRunning() {
        return preferences.getBoolean(PREF_SERVICE, false);
    }

    public void setRunning(boolean running) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_SERVICE, running);
        editor.commit();
    }

    public Unit getUnit() {
        return Units.deserializeUnit(preferences.getInt(PREF_UNIT, 0));
    }

    public void setUnit(Unit unit) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREF_UNIT, Units.serializeUnit(unit));
        editor.commit();
    }

}
