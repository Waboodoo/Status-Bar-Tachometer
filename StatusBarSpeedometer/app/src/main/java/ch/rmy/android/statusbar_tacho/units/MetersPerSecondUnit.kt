package ch.rmy.android.statusbar_tacho.units

import ch.rmy.android.statusbar_tacho.R

class MetersPerSecondUnit : Unit() {

    override val nameRes: Int
        get() = R.string.unit_ms

    override fun convertSpeed(metersPerSecond: Float): Float {
        return metersPerSecond
    }

}
