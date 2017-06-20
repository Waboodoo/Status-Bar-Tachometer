package ch.rmy.android.statusbar_tacho.units

import ch.rmy.android.statusbar_tacho.R

class MetersPerSecondUnit : Unit() {

    override val nameRes: Int
        get() = R.string.unit_ms

    override val maxValue: Int
        get() = 50

    override val steps: Int
        get() = 5

    override fun convertSpeed(metersPerSecond: Float): Float {
        return metersPerSecond
    }

}
