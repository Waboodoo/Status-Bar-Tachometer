package ch.rmy.android.statusbar_tacho.utils

import kotlin.math.cos
import kotlin.math.sin

object Trigonometry {

    fun sin(degrees: Float): Float = sin(toRadians(degrees)).toFloat()

    fun cos(degrees: Float): Float = cos(toRadians(degrees)).toFloat()

    private fun toRadians(degrees: Float): Double = degrees / 180 * Math.PI

}
