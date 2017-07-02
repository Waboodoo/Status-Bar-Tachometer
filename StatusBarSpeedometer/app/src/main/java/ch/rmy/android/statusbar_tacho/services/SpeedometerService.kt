package ch.rmy.android.statusbar_tacho.services

import android.content.Context
import android.content.Intent
import android.support.annotation.StringRes

import ch.rmy.android.statusbar_tacho.R
import ch.rmy.android.statusbar_tacho.icons.IconProvider
import ch.rmy.android.statusbar_tacho.location.SpeedWatcher
import ch.rmy.android.statusbar_tacho.notifications.NotificationProvider
import ch.rmy.android.statusbar_tacho.units.Unit
import ch.rmy.android.statusbar_tacho.utils.Destroyable
import ch.rmy.android.statusbar_tacho.utils.EventSource
import ch.rmy.android.statusbar_tacho.utils.ScreenStateWatcher
import ch.rmy.android.statusbar_tacho.utils.Settings

class SpeedometerService : BaseService() {

    private var speedWatcher: SpeedWatcher? = null
    private var iconProvider: IconProvider? = null
    private var notificationProvider: NotificationProvider? = null
    private var unit: Unit? = null

    override fun onCreate() {
        super.onCreate()

        val settings = Settings(this)
        unit = settings.unit

        iconProvider = IconProvider(context)
        notificationProvider = NotificationProvider(context)
        speedWatcher = destroyer.own(SpeedWatcher(context))
        val screenStateWatcher = destroyer.own(ScreenStateWatcher(context))

        notificationProvider!!.initializeNotification(this)

        speedWatcher!!.speedSource.bind(object : EventSource.Observer<Float?> {
            override fun on(currentSpeed: Float?) {
                updateNotification(currentSpeed)
            }
        }, speedWatcher!!.currentSpeed)

        screenStateWatcher.screenStateSource.bind(object : EventSource.Observer<Boolean> {
            override fun on(isScreenOn: Boolean) {
                if (isScreenOn) {
                    speedWatcher!!.enable()
                } else {
                    speedWatcher!!.disable()
                }
            }
        }, screenStateWatcher.isScreenOn)

        settings.isRunning = true
        destroyer.own(object : Destroyable {
            override fun destroy() {
                settings.isRunning = false
            }
        })
    }

    private fun updateNotification(currentSpeed: Float?) {
        val message: String
        val iconRes: Int
        if (currentSpeed == null) {
            message = getString(specialStateMessage)
            iconRes = R.drawable.icon_unknown
        } else {
            val convertedSpeed = unit!!.convertSpeed(currentSpeed)

            message = getString(
                    R.string.speed_format_with_unit,
                    convertedSpeed,
                    getString(unit!!.nameRes)
            )
            iconRes = iconProvider!!.getIconForNumber(Math.round(convertedSpeed))
        }
        notificationProvider!!.updateNotification(message, iconRes)
    }

    private val specialStateMessage: Int
        @StringRes
        get() {
            if (!speedWatcher!!.isGPSEnabled) {
                return R.string.gps_disabled
            }
            if (!speedWatcher!!.hasLocationPermission()) {
                return R.string.permission_missing
            }
            return R.string.unknown
        }

    companion object {

        fun setRunningState(context: Context, state: Boolean) {
            val intent = Intent(context, SpeedometerService::class.java)
            if (state) {
                context.startService(intent)
            } else {
                context.stopService(intent)
            }
        }

        fun isRunning(context: Context): Boolean {
            return Settings(context).isRunning
        }

        fun restart(context: Context) {
            setRunningState(context, false)
            setRunningState(context, true)
        }
    }

}
