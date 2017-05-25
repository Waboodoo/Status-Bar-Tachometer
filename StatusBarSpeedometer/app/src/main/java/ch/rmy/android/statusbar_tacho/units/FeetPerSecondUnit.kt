package ch.rmy.android.statusbar_tacho.units

import ch.rmy.android.statusbar_tacho.R

class FeetPerSecondUnit : Unit() {

    override val nameRes: Int
        get() = R.string.unit_fts

    override fun convertSpeed(metersPerSecond: Float): Float {
        return metersPerSecond * 3.28084f
    }

}
