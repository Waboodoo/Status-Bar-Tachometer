package ch.rmy.android.statusbar_tacho.activities

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.rmy.android.statusbar_tacho.R
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
    gaugeTheme: GaugeTheme,
    speedLabel: String,
    isRunning: Boolean,
    onClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
) {
    var instructionsTargetAlpha by rememberSaveable {
        mutableFloatStateOf(0f)
    }
    LaunchedEffect(isRunning) {
        if (isRunning) {
            instructionsTargetAlpha = 0f
        } else {
            delay(3.seconds)
            instructionsTargetAlpha = 1f
        }
    }
    val instructionsAlpha by animateFloatAsState(
        targetValue = instructionsTargetAlpha,
    )
    val settingsAlpha by animateFloatAsState(
        targetValue = if (isRunning) 0f else 1f,
    )

    Scaffold(
        modifier = Modifier
            .imePadding()
            .statusBarsPadding(),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .background(colorResource(R.color.main_background))
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClicked,
                    )
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Gauge(
                    modifier = Modifier
                        .padding(20.dp)
                        .weight(1f, fill = false),
                    value = gaugeValue,
                    maxValue = gaugeMaxValue,
                    markCount = gaugeMarkCount,
                    theme = gaugeTheme,
                )

                Text(
                    text = speedLabel,
                    textAlign = TextAlign.Center,
                    fontSize = 48.sp,
                    color = colorResource(R.color.main_foreground),
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

            Icon(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .alpha(settingsAlpha)
                    .clickable(
                        role = Role.Button,
                        onClick = onSettingsClicked,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false),
                    )
                    .padding(20.dp)
                    .size(30.dp),
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.settings_title),
                tint = colorResource(R.color.main_foreground_secondary),
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
        onClicked = {},
        onSettingsClicked = {},
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
        onClicked = {},
        onSettingsClicked = {},
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
        onClicked = {},
        onSettingsClicked = {},
    )
}