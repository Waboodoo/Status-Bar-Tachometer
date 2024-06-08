package ch.rmy.android.statusbar_tacho.views

import androidx.compose.runtime.Stable

enum class GaugeScale(
    @Stable
    val factor: Float,
) {
    FAST(1f),
    MEDIUM(4f),
    SLOW(10f),
    VERY_FAST(0.25f),
}
