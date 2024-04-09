package ch.rmy.android.statusbar_tacho.activities

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ch.rmy.android.statusbar_tacho.R

@Composable
fun WelcomeDialog(
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(stringResource(R.string.button_ok))
            }
        },
        title = {
            Text(stringResource(R.string.intro_title))
        },
        text = {
            Text(stringResource(R.string.intro_message))
        },
    )
}

@Preview
@Composable
private fun WelcomeDialog_Preview() {
    WelcomeDialog(
        onDismissRequest = {},
    )
}