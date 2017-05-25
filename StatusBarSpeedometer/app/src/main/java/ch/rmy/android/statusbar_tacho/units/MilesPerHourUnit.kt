package ch.rmy.android.statusbar_tacho.units

import ch.rmy.android.statusbar_tacho.R

class MilesPerHourUnit : Unit() {

    override val nameRes: Int
        get() = R.string.unit_mph

    override fun convertSpeed(metersPerSecond: Float): Float {
        return metersPerSecond * 2.23694f
    }

}
