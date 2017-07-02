package ch.rmy.android.statusbar_tacho.units

import ch.rmy.android.statusbar_tacho.R

class KilometersPerHourUnit : Unit() {

    override val nameRes: Int
        get() = R.string.unit_kmh

    override val maxValue: Int
        get() = 180

    override val steps: Int
        get() = 9

    override fun convertSpeed(metersPerSecond: Float): Float {
        return metersPerSecond * 3.6f
    }

}
