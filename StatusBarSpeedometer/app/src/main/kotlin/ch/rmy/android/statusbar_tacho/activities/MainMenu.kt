package ch.rmy.android.statusbar_tacho.activities

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.rmy.android.statusbar_tacho.R
import ch.rmy.android.statusbar_tacho.components.Menu
import ch.rmy.android.statusbar_tacho.components.MenuItem

@Composable
fun MainMenu(
    modifier: Modifier,
    onSettingsClicked: () -> Unit,
    onTopSpeedClicked: () -> Unit,
) {
    Menu(modifier) {
        MenuItem(
            title = stringResource(R.string.top_speed_title),
            icon = painterResource(R.drawable.outline_speed_24),
            onClick = onTopSpeedClicked,
        )
        MenuItem(
            title = stringResource(R.string.settings_title),
            icon = painterResource(R.drawable.outline_settings_24),
            onClick = onSettingsClicked,
        )
    }
}
