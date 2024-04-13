package ch.rmy.android.statusbar_tacho.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ScreenStateWatcher(private val context: Context) : Destroyable {

    private val screenStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            isScreenOn = intent.action == Intent.ACTION_SCREEN_ON
        }
    }

    private var isScreenOn: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                _screenState.value = value
            }
        }

    val screenState: StateFlow<Boolean>
        get() = _screenState

    private val _screenState = MutableStateFlow(isScreenOn)

    private val powerManager
        get() = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    init {
        isScreenOn = powerManager.isInteractive
        register()
    }

    private fun register() {
        val filter = IntentFilter()
            .apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_SCREEN_OFF)
            }
        context.registerReceiver(screenStateReceiver, filter)
    }

    override fun destroy() {
        context.unregisterReceiver(screenStateReceiver)
    }

}
