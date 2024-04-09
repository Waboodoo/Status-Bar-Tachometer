package ch.rmy.android.statusbar_tacho.location

sealed interface SpeedUpdate { // TODO: Rename to SpeedState or something

    data class SpeedChanged(val speed: Float) : SpeedUpdate

    object SpeedUnavailable : SpeedUpdate

    object GPSDisabled : SpeedUpdate

    object Disabled : SpeedUpdate

}