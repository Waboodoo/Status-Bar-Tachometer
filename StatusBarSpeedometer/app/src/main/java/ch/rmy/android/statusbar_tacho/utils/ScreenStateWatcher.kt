package ch.rmy.android.statusbar_tacho.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.PowerManager

class ScreenStateWatcher(private val context: Context) : Destroyable {

    var isScreenOn: Boolean = false
        private set

    private val screenStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            isScreenOn = Intent.ACTION_SCREEN_ON == intent.action
            screenStateSource.notify(isScreenOn)
        }
    }
    val screenStateSource = EventSource<Boolean>()

    init {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            isScreenOn = powerManager.isInteractive
        } else {
            isScreenOn = powerManager.isScreenOn
        }
        register()
    }

    private fun register() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        context.registerReceiver(screenStateReceiver, filter)
    }

    override fun destroy() {
        context.unregisterReceiver(screenStateReceiver)
    }

}
