package ch.rmy.android.statusbar_tacho.units

object Units {

    val UNITS = arrayOf(KilometersPerHourUnit(), MilesPerHourUnit(), MetersPerSecondUnit(), FeetPerSecondUnit())

    fun deserializeUnit(unitId: Int): Unit {
        return UNITS[unitId]
    }

    fun serializeUnit(unit: Unit): Int {
        for (i in UNITS.indices) {
            if (UNITS[i].javaClass == unit.javaClass) {
                return i
            }
        }
        throw IllegalArgumentException("Unknown unit")
    }

}
