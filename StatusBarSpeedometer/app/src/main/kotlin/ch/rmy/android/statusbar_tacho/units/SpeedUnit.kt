package ch.rmy.android.statusbar_tacho.units

import androidx.annotation.StringRes
import ch.rmy.android.statusbar_tacho.R

enum class SpeedUnit {

    KILOMETERS_PER_HOUR {

        override val nameRes: Int
            get() = R.string.unit_kmh

        override val longNameRes: Int
            get() = R.string.unit_kmh_long

        override val maxValue: Int
            get() = 180

        override val steps: Int
            get() = 9

        override fun convertSpeed(metersPerSecond: Float): Float =
            metersPerSecond * 3.6f
    },

    METERS_PER_SECOND {
        override val nameRes: Int
            get() = R.string.unit_ms

        override val longNameRes: Int
            get() = R.string.unit_ms_long

        override val maxValue: Int
            get() = 60

        override val steps: Int
            get() = 3

        override fun convertSpeed(metersPerSecond: Float): Float =
            metersPerSecond
    },

    MILES_PER_HOUR {
        override val nameRes: Int
            get() = R.string.unit_mph

        override val longNameRes: Int
            get() = R.string.unit_mph_long

        override val maxValue: Int
            get() = 120

        override val steps: Int
            get() = 6

        override fun convertSpeed(metersPerSecond: Float): Float =
            metersPerSecond * 2.23694f
    },

    FEET_PER_SECOND {
        override val nameRes: Int
            get() = R.string.unit_fts

        override val longNameRes: Int
            get() = R.string.unit_fts_long

        override val maxValue: Int
            get() = 160

        override val steps: Int
            get() = 8

        override fun convertSpeed(metersPerSecond: Float): Float =
            metersPerSecond * 3.28084f
    },

    KNOTS {
        override val nameRes: Int
            get() = R.string.unit_knots

        override val longNameRes: Int
            get() = R.string.unit_knots

        override val maxValue: Int
            get() = 120

        override val steps: Int
            get() = 6

        override fun convertSpeed(metersPerSecond: Float): Float =
            metersPerSecond * 1.94384f
    },
    ;

    @get:StringRes
    abstract val nameRes: Int

    @get:StringRes
    abstract val longNameRes: Int

    abstract val maxValue: Int

    abstract val steps: Int

    abstract fun convertSpeed(metersPerSecond: Float): Float

}