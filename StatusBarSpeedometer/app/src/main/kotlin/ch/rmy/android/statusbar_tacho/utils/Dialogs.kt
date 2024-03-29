package ch.rmy.android.statusbar_tacho.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import ch.rmy.android.statusbar_tacho.R

object Dialogs {

    fun showIntroMessage(context: Context, settings: Settings) {
        if (!settings.isFirstRun) {
            return
        }
        AlertDialog.Builder(context)
            .setTitle(R.string.intro_title)
            .setMessage(R.string.intro_message)
            .setPositiveButton(R.string.button_ok, null)
            .setOnDismissListener {
                settings.isFirstRun = false
            }
            .show()
    }

}