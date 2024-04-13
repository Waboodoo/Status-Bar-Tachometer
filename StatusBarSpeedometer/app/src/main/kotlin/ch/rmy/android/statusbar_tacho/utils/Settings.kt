package ch.rmy.android.statusbar_tacho.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.text.intl.Locale
import androidx.core.content.edit
import ch.rmy.android.statusbar_tacho.units.SpeedUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object Settings {

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        _isRunningFlow.value = preferences.getBoolean(PREF_SERVICE, false)
    }

    private lateinit var preferences: SharedPreferences

    private const val PREF = "pref"
    private const val PREF_SERVICE = "service"
    private const val PREF_SPEED_UNIT = "speed_unit"
    private const val PREF_FIRST_RUN = "first_run"
    private const val PREF_KEEP_UPDATING_WHILE_SCREEN_OFF = "keep_updating_while_screen_off"

    var isRunning: Boolean
        get() = _isRunningFlow.value
        set(running) {
            _isRunningFlow.value = running
            preferences.edit {
                putBoolean(PREF_SERVICE, running)
            }
        }

    private val _isRunningFlow = MutableStateFlow(false)
    val isRunningFlow = _isRunningFlow.asStateFlow()

    var unit: SpeedUnit
        get() = SpeedUnit.valueOf(preferences.getString(PREF_SPEED_UNIT, getDefaultUnit().name)!!)
        set(unit) = preferences.edit {
            putString(PREF_SPEED_UNIT, unit.name)
        }

    private fun getDefaultUnit() =
        if (Locale.current.toLanguageTag() == "en-US") {
            SpeedUnit.MILES_PER_HOUR
        } else {
            SpeedUnit.KILOMETERS_PER_HOUR
        }

    var isFirstRun: Boolean
        get() = preferences.getBoolean(PREF_FIRST_RUN, true)
        set(value) = preferences.edit {
            putBoolean(PREF_FIRST_RUN, value)
        }

    var shouldKeepUpdatingWhileScreenIsOff: Boolean
        get() = preferences.getBoolean(PREF_KEEP_UPDATING_WHILE_SCREEN_OFF, false)
        set(value) = preferences.edit {
            putBoolean(PREF_KEEP_UPDATING_WHILE_SCREEN_OFF, value)
        }

}
