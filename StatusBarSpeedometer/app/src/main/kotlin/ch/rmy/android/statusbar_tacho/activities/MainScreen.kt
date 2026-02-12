package ch.rmy.android.statusbar_tacho.activities

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.rmy.android.statusbar_tacho.R
import ch.rmy.android.statusbar_tacho.utils.Settings
import ch.rmy.android.statusbar_tacho.views.Gauge
import ch.rmy.android.statusbar_tacho.views.GaugeTheme
import ch.rmy.android.statusbar_tacho.views.getGaugeTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun MainScreen(
    gaugeValue: Float,
    gaugeMaxValue: Float,
    gaugeMarkCount: Int,
    gaugeTheme: GaugeTheme?,
    speedLabel: String,
    isRunning: Boolean,
    isInSettings: Boolean,
    onClicked: () -> Unit,
) {
    var instructionsTargetAlpha by rememberSaveable {
        mutableFloatStateOf(0f)
    }
    LaunchedEffect(isRunning, isInSettings) {
        if (isRunning || isInSettings) {
            instructionsTargetAlpha = 0f
        } else {
            delay(if (Settings.topSpeed != null) 10.seconds else 3.seconds)
            instructionsTargetAlpha = 1f
        }
    }
    val instructionsAlpha by animateFloatAsState(
        targetValue = instructionsTargetAlpha,
    )
    val speedLabelAlpha by animateFloatAsState(
        targetValue = if (isRunning) 1f else 0.5f,
    )

    val animatedGaugeValue by animateFloatAsState(
        targetValue = gaugeValue,
        animationSpec = tween(700),
    )
    val animatedGaugeMaxValue by animateFloatAsState(
        targetValue = gaugeMaxValue,
        animationSpec = tween(700),
    )

    Column(
        modifier = Modifier
            .run {
                if (isInSettings) {
                    this
                } else {
                    clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClicked,
                    )
                }
            }
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (gaugeTheme != null) {
            Gauge(
                modifier = Modifier
                    .padding(24.dp)
                    .weight(1f, fill = false),
                value = animatedGaugeValue,
                maxValue = animatedGaugeMaxValue,
                markCount = gaugeMarkCount,
                theme = gaugeTheme,
                showNumbers = gaugeMaxValue == animatedGaugeMaxValue,
            )
        }

        if (!isInSettings) {
            val style = TextStyle(
                fontSize = 56.sp,
                color = colorResource(R.color.main_foreground),
                textAlign = TextAlign.Center,
            )
            BasicText(
                modifier = Modifier
                    .alpha(speedLabelAlpha),
                text = speedLabel,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = 16.sp,
                    maxFontSize = 56.sp,
                ),
                style = style,
            )

            Text(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 16.dp)
                    .alpha(instructionsAlpha),
                text = stringResource(R.string.main_instructions),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = colorResource(R.color.main_foreground_secondary),
            )
        }
    }
}

@Preview
@Composable
private fun MainScreen_Running_Preview() {
    MainScreen(
        gaugeValue = 27f,
        gaugeMaxValue = 80f,
        gaugeMarkCount = 20,
        gaugeTheme = getGaugeTheme(),
        speedLabel = "27.0",
        isRunning = true,
        isInSettings = false,
        onClicked = {},
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MainScreen_Tablet_Preview() {
    MainScreen(
        gaugeValue = 0f,
        gaugeMaxValue = 100f,
        gaugeMarkCount = 20,
        gaugeTheme = getGaugeTheme(),
        speedLabel = "0.0",
        isRunning = false,
        isInSettings = false,
        onClicked = {},
    )
}

@Preview(device = Devices.TABLET)
@Composable
private fun MainScreen_Dark_Preview() {
    MainScreen(
        gaugeValue = 0f,
        gaugeMaxValue = 100f,
        gaugeMarkCount = 20,
        gaugeTheme = getGaugeTheme(),
        speedLabel = "---",
        isRunning = false,
        isInSettings = false,
        onClicked = {},
    )
}