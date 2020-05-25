package ch.rmy.android.statusbar_tacho.utils

import android.content.Context
import ch.rmy.android.statusbar_tacho.R
import ch.rmy.android.statusbar_tacho.units.SpeedUnit

object SpeedFormatter {

    fun formatSpeed(context: Context, speed: Float, unit: SpeedUnit? = null): String {
        val speedString = if (speed < 100f) {
            String.format("%1$.1f", speed)
        } else {
            String.format("%1$.0f", speed)
        }
        return unit
            ?.let {
                context.getString(
                    R.string.speed_format_with_unit,
                    speedString,
                    context.getString(it.nameRes)
                )
            }
            ?: speedString
    }

}