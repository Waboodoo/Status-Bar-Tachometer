package ch.rmy.android.statusbar_tacho.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import ch.rmy.android.statusbar_tacho.R
import ch.rmy.android.statusbar_tacho.extensions.context
import ch.rmy.android.statusbar_tacho.extensions.ownedBy
import ch.rmy.android.statusbar_tacho.icons.IconProvider
import ch.rmy.android.statusbar_tacho.location.SpeedUpdate
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

    override fun onCreate() {
        super.onCreate()

        notificationProvider.initializeNotification(this)

        scope.launch {
            updateNotification(SpeedUpdate.SpeedUnavailable)
            speedWatcher.speedUpdates.collect { speedUpdate ->
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

    private fun updateNotification(speedUpdate: SpeedUpdate) {
        val convertedSpeed = (speedUpdate as? SpeedUpdate.SpeedChanged)?.speed?.let(unit::convertSpeed) ?: 0.0f

        val message = when (speedUpdate) {
            is SpeedUpdate.SpeedChanged -> SpeedFormatter.formatSpeed(context, convertedSpeed, unit)
            is SpeedUpdate.GPSDisabled -> getString(R.string.gps_disabled)
            is SpeedUpdate.SpeedUnavailable,
            is SpeedUpdate.Disabled,
            -> getString(R.string.unknown)
        }
        val iconRes = when (speedUpdate) {
            is SpeedUpdate.SpeedChanged -> iconProvider.getIconForNumber(convertedSpeed.roundToInt())
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

        fun toggleRunningState(context: Context) {
            setRunningState(context, !Settings.isRunning)
        }
    }

}
