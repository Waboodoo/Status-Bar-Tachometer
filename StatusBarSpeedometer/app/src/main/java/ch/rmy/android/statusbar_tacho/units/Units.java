package ch.rmy.android.statusbar_tacho.units;

public class Units {

    public static final Unit[] UNITS = {
            new KilometersPerHourUnit(),
            new MilesPerHourUnit(),
            new MetersPerSecondUnit(),
            new FeetPerSecondUnit(),
    };

    public static Unit deserializeUnit(int unitId) {
        return UNITS[unitId];
    }

    public static int serializeUnit(Unit unit) {
        for (int i = 0; i < UNITS.length; i++) {
            if (UNITS[i].getClass() == unit.getClass()) {
                return i;
            }
        }
        throw new IllegalArgumentException("Unknown unit");
    }

}
