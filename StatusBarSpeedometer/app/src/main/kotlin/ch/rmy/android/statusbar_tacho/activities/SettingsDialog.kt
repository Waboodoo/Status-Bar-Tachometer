package ch.rmy.android.statusbar_tacho.activities


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.rmy.android.statusbar_tacho.R
import ch.rmy.android.statusbar_tacho.extensions.clickOnlyInteractionSource
import ch.rmy.android.statusbar_tacho.units.SpeedUnit

@Composable
fun SettingsDialog(
    speedUnit: SpeedUnit,
    runWhenScreenOff: Boolean,
    onSpeedUnitChanged: (SpeedUnit) -> Unit,
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
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
    ) {
        OutlinedTextField(
            value = stringResource(speedUnit.nameRes),
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = {
                Text(stringResource(R.string.label_unit))
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        expanded = true
                    },
                ) {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null,
                    )
                }
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
            SpeedUnit.entries.forEach {
                DropdownMenuItem(
                    text = {
                        Text(stringResource(id = it.nameRes))
                    },
                    onClick = {
                        onSpeedUnitChanged(it)
                        expanded = false
                    },
                )
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
        modifier = Modifier.toggleable(
            value = runWhenScreenOff,
            onValueChange = onRunWhenScreenOffChanged,
            role = Role.Switch,
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Switch(
            modifier = Modifier,
            checked = runWhenScreenOff,
            onCheckedChange = null,
        )
        Text(
            text = stringResource(R.string.checkbox_label_keep_updating_when_screen_off),
        )
    }
}

@Preview
@Composable
private fun SettingsDialog_Preview() {
    SettingsDialog(
        speedUnit = SpeedUnit.KILOMETERS_PER_HOUR,
        runWhenScreenOff = false,
        onSpeedUnitChanged = {},
        onRunWhenScreenOffChanged = {},
        onDismissRequest = {},
    )
}