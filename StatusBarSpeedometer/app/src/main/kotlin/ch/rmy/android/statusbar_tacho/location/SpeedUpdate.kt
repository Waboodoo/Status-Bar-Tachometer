package ch.rmy.android.statusbar_tacho.location

sealed class SpeedUpdate {

    class SpeedChanged(val speed: Float) : SpeedUpdate()

    object SpeedUnavailable : SpeedUpdate()

}