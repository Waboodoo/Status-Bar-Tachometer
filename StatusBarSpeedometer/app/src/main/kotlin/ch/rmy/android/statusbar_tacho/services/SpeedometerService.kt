package ch.rmy.android.statusbar_tacho.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import ch.rmy.android.statusbar_tacho.R
import ch.rmy.android.statusbar_tacho.extensions.context
import ch.rmy.android.statusbar_tacho.extensions.ownedBy
import ch.rmy.android.statusbar_tacho.icons.IconProvider
import ch.rmy.android.statusbar_tacho.location.SpeedState
import ch.rmy.android.statusbar_tacho.location.SpeedWatcher
import ch.rmy.android.statusbar_tacho.notifications.NotificationProvider
import ch.rmy.android.statusbar_tacho.units.SpeedUnit
import ch.rmy.android.statusbar_tacho.utils.Destroyer
import ch.rmy.android.statusbar_tacho.utils.ScreenStateWatcher
import ch.rmy.android.statusbar_tacho.utils.Settings
import ch.rmy.android.statusbar_tacho.utils.SpeedFormatter
import kotlinx.coroutines.*
import kotlin.math.roundToInt

class SpeedometerService : Service() {

    private val destroyer = Destroyer()

    private val speedWatcher: SpeedWatcher by lazy {
        destroyer.own(SpeedWatcher(context))
    }
    private val iconProvider: IconProvider by lazy {
        IconProvider(context)
    }
    private val notificationProvider: NotificationProvider by lazy {
        NotificationProvider(context)
    }
    private val screenStateWatcher: ScreenStateWatcher by lazy {
        destroyer.own(ScreenStateWatcher(context))
    }

    private val settings: Settings
        get() = Settings

    private val unit: SpeedUnit
        get() = settings.unit

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        ServiceCompat.startForeground(
            this,
            NotificationProvider.NOTIFICATION_ID,
            notificationProvider.getInitialNotification(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            } else {
                0
            },
        )

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()

        scope.launch {
            updateNotification(SpeedState.SpeedUnavailable)
            speedWatcher.speedState.collect { speedUpdate ->
                updateNotification(speedUpdate)
            }
        }
            .ownedBy(destroyer)

        if (settings.shouldKeepUpdatingWhileScreenIsOff) {
            speedWatcher.enable()
        } else {
            setupScreenStateWatcher()
        }

        settings.isRunning = true
        destroyer.own {
            settings.isRunning = false
        }
    }

    private fun setupScreenStateWatcher() {
        scope.launch {
            screenStateWatcher.screenState.collect { isScreenOn ->
                if (isScreenOn) {
                    speedWatcher.enable()
                } else {
                    speedWatcher.disable()
                }
            }
        }
            .ownedBy(destroyer)
    }

    private fun updateNotification(speedState: SpeedState) {
        val convertedSpeed = (speedState as? SpeedState.SpeedChanged)?.speed?.let(unit::convertSpeed) ?: 0.0f

        val message = when (speedState) {
            is SpeedState.SpeedChanged -> SpeedFormatter.formatSpeed(context, convertedSpeed, unit)
            is SpeedState.GPSDisabled -> getString(R.string.gps_disabled)
            is SpeedState.SpeedUnavailable,
            is SpeedState.Disabled,
            -> getString(R.string.unknown)
        }
        val iconRes = when (speedState) {
            is SpeedState.SpeedChanged -> iconProvider.getIconForNumber(convertedSpeed.roundToInt())
            else -> R.drawable.icon_unknown
        }
        notificationProvider.updateNotification(message, iconRes)
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        destroyer.destroy()
    }

    companion object {

        fun setRunningState(context: Context, state: Boolean) {
            val intent = Intent(context, SpeedometerService::class.java)
            if (state) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } else {
                context.stopService(intent)
            }
        }
    }

}
