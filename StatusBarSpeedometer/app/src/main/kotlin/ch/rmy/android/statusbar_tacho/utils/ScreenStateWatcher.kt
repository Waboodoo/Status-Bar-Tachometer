package ch.rmy.android.statusbar_tacho.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.PowerManager
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class ScreenStateWatcher(private val context: Context) : Destroyable {

    private val screenStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            isScreenOn = Intent.ACTION_SCREEN_ON == intent.action

        }
    }

    private var isScreenOn: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                screenStateSubject.onNext(isScreenOn)
            }
        }

    val screenState: Observable<Boolean>
        get() = screenStateSubject

    private val screenStateSubject = BehaviorSubject.createDefault(isScreenOn)

    private val powerManager
        get() = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    init {
        isScreenOn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            powerManager.isInteractive
        } else {
            @Suppress("DEPRECATION")
            powerManager.isScreenOn
        }
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
