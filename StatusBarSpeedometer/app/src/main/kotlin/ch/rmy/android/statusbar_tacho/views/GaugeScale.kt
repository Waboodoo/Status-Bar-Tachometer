package ch.rmy.android.statusbar_tacho.views

import androidx.compose.runtime.Stable

enum class GaugeScale(
    @Stable
    val factor: Int,
) {
    FAST(1),
    MEDIUM(4),
    SLOW(10),
}
