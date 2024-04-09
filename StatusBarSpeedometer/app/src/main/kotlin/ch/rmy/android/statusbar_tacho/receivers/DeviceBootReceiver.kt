package ch.rmy.android.statusbar_tacho.receivers


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ch.rmy.android.statusbar_tacho.services.SpeedometerService
import ch.rmy.android.statusbar_tacho.utils.Settings

class DeviceBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }
        if (Settings.isRunning) {
            SpeedometerService.setRunningState(context, true)
        }
    }

}