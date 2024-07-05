package ch.rmy.android.statusbar_tacho.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.NoOpUpdate

@Composable
fun Gauge(
    modifier: Modifier = Modifier,
    value: Float,
    maxValue: Float,
    markCount: Int,
    theme: GaugeTheme,
    showNumbers: Boolean,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            GaugeView(context, value, maxValue, markCount, theme)
        },
        update = { gaugeView ->
            gaugeView.value = value
            gaugeView.maxValue = maxValue
            gaugeView.markCount = markCount
            gaugeView.theme = theme
            gaugeView.showNumbers = showNumbers
        },
        onReset = NoOpUpdate,
    )
}