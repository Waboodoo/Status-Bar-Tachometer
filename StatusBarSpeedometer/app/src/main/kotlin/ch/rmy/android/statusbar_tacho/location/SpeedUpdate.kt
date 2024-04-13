package ch.rmy.android.statusbar_tacho.location

sealed interface SpeedUpdate { // TODO: Rename to SpeedState or something

    data class SpeedChanged(val speed: Float) : SpeedUpdate

    data object SpeedUnavailable : SpeedUpdate

    data object GPSDisabled : SpeedUpdate

    data object Disabled : SpeedUpdate

}