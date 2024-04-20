package ch.rmy.android.statusbar_tacho.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ch.rmy.android.statusbar_tacho.services.SpeedometerService
import ch.rmy.android.statusbar_tacho.utils.Settings

class DismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != DISMISS_ACTION) {
            return
        }
        if (Settings.isRunning) {
            Settings.isRunning = false
            SpeedometerService.setRunningState(context, false)
        }
    }

    companion object {
        const val DISMISS_ACTION = "ch.rmy.android.statusbar_tacho.NOTIFICATION_DISMISSED"
    }
}
