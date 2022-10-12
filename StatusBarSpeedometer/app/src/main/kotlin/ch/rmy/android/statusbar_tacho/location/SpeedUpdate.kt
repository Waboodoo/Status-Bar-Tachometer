package ch.rmy.android.statusbar_tacho.location

sealed interface SpeedUpdate {

    data class SpeedChanged(val speed: Float) : SpeedUpdate

    object SpeedUnavailable : SpeedUpdate

    object GPSDisabled : SpeedUpdate

    object Disabled : SpeedUpdate

}