package ch.rmy.android.statusbar_tacho.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import ch.rmy.android.statusbar_tacho.R

@Composable
fun Menu(
    modifier: Modifier,
    content: @Composable MenuScope.() -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier) {
        Icon(
            modifier = Modifier
                .clickable(
                    role = Role.Button,
                    onClick = { expanded = !expanded },
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false),
                )
                .padding(20.dp)
                .size(30.dp),
            painter = painterResource(R.drawable.outline_more_vert_24),
            contentDescription = stringResource(R.string.main_menu),
            tint = colorResource(R.color.main_foreground_secondary),
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            with(MenuScope {
                expanded = false
            }) {
                content()
            }
        }
    }
}

fun interface MenuScope {
    fun onItemSelected()
}

@Composable
fun MenuScope.MenuItem(
    title: String,
    icon: Painter,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Text(
                modifier = Modifier.padding(end = 8.dp),
                text = title,
            )
        },
        leadingIcon = {
            Icon(
                modifier = Modifier.padding(start = 8.dp),
                painter = icon,
                contentDescription = null,
            )
        },
        onClick = {
            onClick()
            onItemSelected()
        },
    )
}
