package ch.rmy.android.statusbar_tacho.services

import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import ch.rmy.android.statusbar_tacho.R
import ch.rmy.android.statusbar_tacho.extensions.context
import ch.rmy.android.statusbar_tacho.icons.IconProvider
import ch.rmy.android.statusbar_tacho.location.SpeedState
import ch.rmy.android.statusbar_tacho.location.SpeedWatcher
import ch.rmy.android.statusbar_tacho.notifications.NotificationProvider
import ch.rmy.android.statusbar_tacho.units.SpeedUnit
import ch.rmy.android.statusbar_tacho.utils.Destroyer
import ch.rmy.android.statusbar_tacho.utils.ScreenStateWatcher
import ch.rmy.android.statusbar_tacho.utils.Settings
import ch.rmy.android.statusbar_tacho.utils.SpeedFormatter
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class SpeedometerService : LifecycleService() {

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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
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
        } catch (_: Exception) {
            settings.isRunning = false
            stopSelf()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()

        lifecycleScope.launch {
            updateNotification(SpeedState.SpeedUnavailable)
            speedWatcher.speedState.collect { speedUpdate ->
                updateNotification(speedUpdate)
                updateTopSpeed(speedUpdate)
            }
        }

        if (settings.shouldKeepUpdatingWhileScreenIsOff) {
            speedWatcher.enable()
        } else {
            setupScreenStateWatcher()
        }

        settings.isRunning = true
    }

    private fun setupScreenStateWatcher() {
        lifecycleScope.launch {
            screenStateWatcher.screenState.collect { isScreenOn ->
                if (isScreenOn) {
                    speedWatcher.enable()
                } else {
                    speedWatcher.disable()
                }
            }
        }
    }

    private fun updateNotification(speedState: SpeedState) {
        val convertedSpeed = (speedState as? SpeedState.SpeedChanged)?.speed?.let(unit::convertSpeed) ?: 0.0f

        val message = when (speedState) {
            is SpeedState.SpeedChanged -> SpeedFormatter.formatSpeed(context, convertedSpeed, unit)
            is SpeedState.GPSDisabled -> getString(R.string.gps_disabled)
            is SpeedState.SpeedUnavailable -> getString(R.string.unknown)
            is SpeedState.Disabled -> return
        }
        val iconRes = when (speedState) {
            is SpeedState.SpeedChanged -> iconProvider.getIconForNumber(convertedSpeed.roundToInt())
            else -> R.drawable.icon_unknown
        }
        notificationProvider.updateNotification(message, iconRes)
    }

    private fun updateTopSpeed(speedState: SpeedState) {
        if (speedState !is SpeedState.SpeedChanged) return
        val previousTopSpeed = settings.topSpeed
        if (previousTopSpeed == null || speedState.speed > previousTopSpeed) {
            settings.topSpeed = speedState.speed
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyer.destroy()
        settings.isRunning = false
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
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
