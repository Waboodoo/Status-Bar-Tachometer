package ch.rmy.android.statusbar_tacho.utils

import android.content.Context
import android.content.SharedPreferences
import ch.rmy.android.statusbar_tacho.extensions.edit
import ch.rmy.android.statusbar_tacho.units.SpeedUnit

class Settings(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)

    var isRunning: Boolean
        get() = preferences.getBoolean(PREF_SERVICE, false)
        set(running) = preferences.edit {
            putBoolean(PREF_SERVICE, running)
        }

    var unit: SpeedUnit
        get() = SpeedUnit.valueOf(preferences.getString(PREF_SPEED_UNIT, SpeedUnit.KILOMETERS_PER_HOUR.name)!!)
        set(unit) = preferences.edit {
            putString(PREF_SPEED_UNIT, unit.name)
        }

    var isFirstRun: Boolean
        get() = preferences.getBoolean(PREF_FIRST_RUN, true)
        set(value) = preferences.edit {
            putBoolean(PREF_FIRST_RUN, value)
        }

    companion object {

        private const val PREF = "pref"
        private const val PREF_SERVICE = "service"
        private const val PREF_SPEED_UNIT = "speed_unit"
        private const val PREF_FIRST_RUN = "first_run"

    }

}
