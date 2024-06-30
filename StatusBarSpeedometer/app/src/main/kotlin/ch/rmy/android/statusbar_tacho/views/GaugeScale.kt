package ch.rmy.android.statusbar_tacho.views

import androidx.compose.runtime.Stable

enum class GaugeScale(
    @Stable
    val factor: Float?,
) {
    FAST(1f),
    MEDIUM(4f),
    SLOW(10f),
    VERY_FAST(0.25f),
    DYNAMIC(null),
    ;

    companion object {
        val FACTORS = arrayOf(10f, 4f, 1f, 0.25f, 0.125f, 0.0625f)
    }
}