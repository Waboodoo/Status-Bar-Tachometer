package ch.rmy.android.statusbar_tacho.extensions

import android.app.Activity
import android.app.Service
import android.content.Context
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import ch.rmy.android.statusbar_tacho.utils.Destroyer
import kotlinx.coroutines.Job

fun Job.ownedBy(destroyer: Destroyer) {
    destroyer.own {
        cancel()
    }
}

val Activity.context: Context
    get() = this

val Service.context: Context
    get() = this

@Composable
fun clickOnlyInteractionSource(onClick: () -> Unit) = remember { MutableInteractionSource() }
    .also { interactionSource ->
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect {
                if (it is PressInteraction.Release) {
                    onClick()
                }
            }
        }
    }