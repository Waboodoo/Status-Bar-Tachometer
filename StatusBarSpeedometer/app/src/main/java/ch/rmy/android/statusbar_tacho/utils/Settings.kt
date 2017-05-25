package ch.rmy.android.statusbar_tacho.utils

import android.content.Context
import android.content.SharedPreferences

import ch.rmy.android.statusbar_tacho.units.Unit
import ch.rmy.android.statusbar_tacho.units.Units

class Settings(context: Context) {

    private val preferences: SharedPreferences

    init {
        preferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
    }

    var isRunning: Boolean
        get() = preferences.getBoolean(PREF_SERVICE, false)
        set(running) {
            val editor = preferences.edit()
            editor.putBoolean(PREF_SERVICE, running)
            editor.commit()
        }

    var unit: Unit
        get() = Units.deserializeUnit(preferences.getInt(PREF_UNIT, 0))
        set(unit) {
            val editor = preferences.edit()
            editor.putInt(PREF_UNIT, Units.serializeUnit(unit))
            editor.commit()
        }

    companion object {

        private val PREF = "pref"
        private val PREF_SERVICE = "service"
        private val PREF_UNIT = "unit"
    }

}
