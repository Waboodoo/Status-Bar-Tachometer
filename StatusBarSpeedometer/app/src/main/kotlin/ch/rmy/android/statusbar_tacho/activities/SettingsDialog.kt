package ch.rmy.android.statusbar_tacho.activities


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.rmy.android.statusbar_tacho.R
import ch.rmy.android.statusbar_tacho.extensions.clickOnlyInteractionSource
import ch.rmy.android.statusbar_tacho.units.SpeedUnit
import ch.rmy.android.statusbar_tacho.views.GaugeScale
import ch.rmy.android.statusbar_tacho.views.ThemeId

@Composable
fun SettingsDialog(
    speedUnit: SpeedUnit,
    themeId: ThemeId,
    gaugeScale: GaugeScale,
    runWhenScreenOff: Boolean,
    onSpeedUnitChanged: (SpeedUnit) -> Unit,
    onThemeIdChanged: (ThemeId) -> Unit,
    onGaugeScaleChanged: (GaugeScale) -> Unit,
    onRunWhenScreenOffChanged: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(stringResource(R.string.button_close))
            }
        },
        title = {
            Text(stringResource(R.string.settings_title))
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SpeedUnitPicker(
                    speedUnit = speedUnit,
                    onSpeedUnitChanged = onSpeedUnitChanged
                )

                ThemeIdPicker(
                    themeId = themeId,
                    onThemeIdChanged = onThemeIdChanged,
                )

                GaugeScalePicker(
                    gaugeScale = gaugeScale,
                    onGaugeScaleChanged = onGaugeScaleChanged,
                )

                ScreenBehaviorPicker(
                    runWhenScreenOff = runWhenScreenOff,
                    onRunWhenScreenOffChanged = onRunWhenScreenOffChanged,
                )
            }
        }
    )
}

@Composable
private fun SpeedUnitPicker(
    speedUnit: SpeedUnit,
    onSpeedUnitChanged: (SpeedUnit) -> Unit,
) {
    DropdownField(
        label = stringResource(R.string.label_unit),
        value = stringResource(speedUnit.longNameRes),
    ) { collapse ->
        SpeedUnit.entries.forEach {
            DropdownMenuItem(
                text = {
                    Text(stringResource(id = it.longNameRes))
                },
                onClick = {
                    onSpeedUnitChanged(it)
                    collapse()
                },
            )
        }
    }
}

@Composable
private fun ThemeIdPicker(
    themeId: ThemeId,
    onThemeIdChanged: (ThemeId) -> Unit,
) {
    DropdownField(
        label = stringResource(R.string.label_gauge_theme),
        value = getThemeName(themeId),
    ) { collapse ->
        ThemeId.entries.forEach {
            DropdownMenuItem(
                text = {
                    Text(getThemeName(it))
                },
                onClick = {
                    onThemeIdChanged(it)
                    collapse()
                },
            )
        }
    }
}

@Composable
private fun GaugeScalePicker(
    gaugeScale: GaugeScale,
    onGaugeScaleChanged: (GaugeScale) -> Unit,
) {
    DropdownField(
        label = stringResource(R.string.label_gauge_scale),
        value = getGaugeScaleName(gaugeScale, withEmoji = true),
        semanticValue = getGaugeScaleName(gaugeScale, withEmoji = false),
    ) { collapse ->
        GaugeScale.entries.forEach {
            val semanticText = getGaugeScaleName(it, withEmoji = false)
            DropdownMenuItem(
                text = {
                    Text(
                        modifier = Modifier.semantics {
                            contentDescription = semanticText
                        },
                        text = getGaugeScaleName(it, withEmoji = true),
                    )
                },
                onClick = {
                    onGaugeScaleChanged(it)
                    collapse()
                },
            )
        }
    }
}

@Stable
@Composable
private fun getThemeName(themeId: ThemeId): String =
    when (themeId) {
        ThemeId.DEFAULT -> stringResource(R.string.theme_name_default)
        ThemeId.BLUE -> stringResource(R.string.theme_name_blue)
        ThemeId.RED -> stringResource(R.string.theme_name_red)
        ThemeId.BLACK_AND_WHITE -> stringResource(R.string.theme_name_black_and_white)
    }

@Stable
@Composable
private fun getGaugeScaleName(gaugeScale: GaugeScale, withEmoji: Boolean): String =
    when (gaugeScale) {
        GaugeScale.SLOW -> stringResource(R.string.gauge_scale_slow) + if (withEmoji) " \uD83D\uDEB6" else ""
        GaugeScale.MEDIUM -> stringResource(R.string.gauge_scale_medium) + if (withEmoji) " \uD83D\uDEB2" else ""
        GaugeScale.FAST -> stringResource(R.string.gauge_scale_fast) + if (withEmoji) " \uD83D\uDE97" else ""
    }

@Composable
private fun DropdownField(
    label: String,
    value: String,
    semanticValue: String = value,
    menuContent: @Composable (collapse: () -> Unit) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
    ) {
        OutlinedTextField(
            modifier = Modifier.clearAndSetSemantics {
                text = AnnotatedString("$label: $semanticValue")
                role = Role.DropdownList
            },
            value = value,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = {
                Text(label)
            },
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                )
            },
            interactionSource = clickOnlyInteractionSource(
                onClick = {
                    expanded = true
                },
            ),
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
        ) {
            menuContent {
                expanded = false
            }
        }
    }
}

@Composable
private fun ScreenBehaviorPicker(
    runWhenScreenOff: Boolean,
    onRunWhenScreenOffChanged: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = runWhenScreenOff,
                onValueChange = onRunWhenScreenOffChanged,
                role = Role.Switch,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.checkbox_label_keep_updating_when_screen_off),
        )
        Switch(
            modifier = Modifier,
            checked = runWhenScreenOff,
            onCheckedChange = null,
        )
    }
}

@Preview
@Composable
private fun SettingsDialog_Preview() {
    SettingsDialog(
        speedUnit = SpeedUnit.KILOMETERS_PER_HOUR,
        themeId = ThemeId.DEFAULT,
        gaugeScale = GaugeScale.FAST,
        runWhenScreenOff = false,
        onSpeedUnitChanged = {},
        onThemeIdChanged = {},
        onGaugeScaleChanged = {},
        onRunWhenScreenOffChanged = {},
        onDismissRequest = {},
    )
}