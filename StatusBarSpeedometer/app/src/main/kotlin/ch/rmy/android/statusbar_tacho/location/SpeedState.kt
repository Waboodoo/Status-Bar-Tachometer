package ch.rmy.android.statusbar_tacho.location

sealed interface SpeedState {
    data class SpeedChanged(val speed: Float) : SpeedState
    data object SpeedUnavailable : SpeedState
    data object GPSDisabled : SpeedState
    data object Disabled : SpeedState
}
