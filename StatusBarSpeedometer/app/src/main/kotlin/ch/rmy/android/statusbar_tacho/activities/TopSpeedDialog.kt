package ch.rmy.android.statusbar_tacho.activities

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.rmy.android.statusbar_tacho.R
import ch.rmy.android.statusbar_tacho.utils.Settings
import ch.rmy.android.statusbar_tacho.utils.SpeedFormatter

@Composable
fun TopSpeedDialog(
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    val topSpeed by Settings.topSpeedFlow.collectAsStateWithLifecycle()
    val unit by Settings.unitFlow.collectAsStateWithLifecycle()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(stringResource(R.string.button_close))
            }
        },
        dismissButton = {
            TextButton(
                enabled = topSpeed != null && topSpeed != 0f,
                onClick = {
                    Settings.topSpeed = null
                },
            ) {
                Text(stringResource(R.string.button_reset))
            }
        },
        title = {
            Text(stringResource(R.string.top_speed_title))
        },
        text = {
            val convertedSpeed = topSpeed?.let(unit::convertSpeed) ?: 0.0f
            Text(
                text = SpeedFormatter.formatSpeed(context, convertedSpeed, unit),
                fontSize = 20.sp,
            )
        },
    )
}

@Preview
@Composable
private fun TopSpeedDialog_Preview() {
    TopSpeedDialog(
        onDismissRequest = {},
    )
}
