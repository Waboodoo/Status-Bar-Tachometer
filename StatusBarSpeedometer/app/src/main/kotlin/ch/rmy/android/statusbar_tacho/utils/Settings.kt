package ch.rmy.android.statusbar_tacho.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.intl.Locale
import androidx.core.content.edit
import ch.rmy.android.statusbar_tacho.units.SpeedUnit
import ch.rmy.android.statusbar_tacho.views.GaugeScale
import ch.rmy.android.statusbar_tacho.views.ThemeId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object Settings {

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        _isRunningFlow.value = preferences.getBoolean(PREF_SERVICE, false)
        _unitFlow.value = SpeedUnit.valueOf(preferences.getString(PREF_SPEED_UNIT, getDefaultUnit().name)!!)
        _themeIdFlow.value = ThemeId.entries.getOrElse(preferences.getInt(PREF_THEME, 0)) { ThemeId.DEFAULT }
        _gaugeScaleFlow.value = GaugeScale.entries.getOrElse(preferences.getInt(PREF_GAUGE_SCALE, 0)) { GaugeScale.FAST }
        _topSpeedFlow.value = preferences.getFloat(PREF_TOP_SPEED, -1f).takeUnless { it == -1f }
    }

    private lateinit var preferences: SharedPreferences

    private const val PREF = "pref"
    private const val PREF_SERVICE = "service"
    private const val PREF_SPEED_UNIT = "speed_unit"
    private const val PREF_FIRST_RUN = "first_run"
    private const val PREF_KEEP_UPDATING_WHILE_SCREEN_OFF = "keep_updating_while_screen_off"
    private const val PREF_THEME = "theme"
    private const val PREF_GAUGE_SCALE = "gauge_scale"
    private const val PREF_TOP_SPEED = "top_speed"

    var isRunning: Boolean
        get() = _isRunningFlow.value
        set(running) {
            _isRunningFlow.value = running
            preferences.edit {
                putBoolean(PREF_SERVICE, running)
            }
        }

    private val _isRunningFlow = MutableStateFlow(false)

    @Stable
    val isRunningFlow = _isRunningFlow.asStateFlow()

    private val _unitFlow = MutableStateFlow(SpeedUnit.METERS_PER_SECOND)

    @Stable
    val unitFlow = _unitFlow.asStateFlow()

    var unit: SpeedUnit
        get() = _unitFlow.value
        set(value) = preferences.edit {
            _unitFlow.value = value
            putString(PREF_SPEED_UNIT, value.name)
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

    private val _themeIdFlow = MutableStateFlow(ThemeId.DEFAULT)

    @Stable
    val themeIdFlow = _themeIdFlow.asStateFlow()

    var themeId: ThemeId
        get() = _themeIdFlow.value
        set(value) = preferences.edit {
            _themeIdFlow.value = value
            putInt(PREF_THEME, value.ordinal)
        }

    private val _gaugeScaleFlow = MutableStateFlow(GaugeScale.FAST)

    @Stable
    val gaugeScaleFlow = _gaugeScaleFlow.asStateFlow()

    var gaugeScale: GaugeScale
        get() = _gaugeScaleFlow.value
        set(value) = preferences.edit {
            _gaugeScaleFlow.value = value
            putInt(PREF_GAUGE_SCALE, value.ordinal)
        }

    private val _topSpeedFlow = MutableStateFlow<Float?>(null)

    @Stable
    val topSpeedFlow = _topSpeedFlow.asStateFlow()

    var topSpeed: Float?
        get() = _topSpeedFlow.value
        set(value) = preferences.edit {
            _topSpeedFlow.value = value
            if (value != null) {
                putFloat(PREF_TOP_SPEED, value)
            } else {
                remove(PREF_TOP_SPEED)
            }
        }

}
