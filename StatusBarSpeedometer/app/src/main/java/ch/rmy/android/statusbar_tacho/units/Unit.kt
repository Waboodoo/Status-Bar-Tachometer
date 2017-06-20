package ch.rmy.android.statusbar_tacho.units

import android.support.annotation.StringRes

abstract class Unit {

    @get:StringRes
    abstract val nameRes: Int

    abstract val maxValue: Int

    abstract val steps: Int

    abstract fun convertSpeed(metersPerSecond: Float): Float

}
