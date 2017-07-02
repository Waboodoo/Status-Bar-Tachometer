package ch.rmy.android.statusbar_tacho.utils

object Trigo {

    fun sin(degrees: Float): Float {
        return Math.sin(toRadians(degrees)).toFloat()
    }

    fun cos(degrees: Float): Float {
        return Math.cos(toRadians(degrees)).toFloat()
    }

    fun toRadians(degrees: Float): Double {
        return degrees / 180 * Math.PI
    }

}
