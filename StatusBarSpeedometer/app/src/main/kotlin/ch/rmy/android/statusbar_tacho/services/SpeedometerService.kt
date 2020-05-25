package ch.rmy.android.statusbar_tacho.services

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.StringRes
import ch.rmy.android.statusbar_tacho.R
import ch.rmy.android.statusbar_tacho.extensions.context
import ch.rmy.android.statusbar_tacho.extensions.ownedBy
import ch.rmy.android.statusbar_tacho.icons.IconProvider
import ch.rmy.android.statusbar_tacho.location.SpeedUpdate
import ch.rmy.android.statusbar_tacho.location.SpeedWatcher
import ch.rmy.android.statusbar_tacho.notifications.NotificationProvider
import ch.rmy.android.statusbar_tacho.units.SpeedUnit
import ch.rmy.android.statusbar_tacho.utils.ScreenStateWatcher
import ch.rmy.android.statusbar_tacho.utils.Settings
import ch.rmy.android.statusbar_tacho.utils.SpeedFormatter
import kotlin.math.roundToInt

class SpeedometerService : BaseService() {

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

    private val settings by lazy {
        Settings(this)
    }

    private val unit: SpeedUnit
        get() = settings.unit

    override fun onCreate() {
        super.onCreate()

        notificationProvider.initializeNotification(this)

        speedWatcher.speedUpdates
            .subscribe {
                updateNotification((it as? SpeedUpdate.SpeedChanged)?.speed)
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
        screenStateWatcher.screenState
            .subscribe { isScreenOn ->
                if (isScreenOn) {
                    speedWatcher.enable()
                } else {
                    speedWatcher.disable()
                }
            }
            .ownedBy(destroyer)
    }

    private fun updateNotification(currentSpeed: Float?) {
        val message: String
        val iconRes: Int
        if (currentSpeed == null) {
            message = getString(specialStateMessage)
            iconRes = R.drawable.icon_unknown
        } else {
            val convertedSpeed = unit.convertSpeed(currentSpeed)

            message = SpeedFormatter.formatSpeed(context, convertedSpeed, unit)
            iconRes = iconProvider.getIconForNumber(convertedSpeed.roundToInt())
        }
        notificationProvider.updateNotification(message, iconRes)
    }

    private val specialStateMessage: Int
        @StringRes
        get() = when {
            !speedWatcher.isGPSEnabled -> R.string.gps_disabled
            !speedWatcher.hasLocationPermission() -> R.string.permission_missing
            else -> R.string.unknown
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
            setRunningState(context, !isRunning(context))
        }

        fun isRunning(context: Context): Boolean =
            Settings(context).isRunning

        fun restart(context: Context) {
            setRunningState(context, false)
            setRunningState(context, true)
        }
    }

}
