package ch.rmy.android.statusbar_tacho.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import androidx.core.content.getSystemService
import androidx.core.location.LocationListenerCompat
import ch.rmy.android.statusbar_tacho.utils.Destroyable
import ch.rmy.android.statusbar_tacho.utils.PermissionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SpeedWatcher(context: Context) : Destroyable {

    private val locationManager: LocationManager =
        context.getSystemService()!!

    val speedUpdates: StateFlow<SpeedUpdate>
        get() = _speedUpdates

    private val _speedUpdates = MutableStateFlow<SpeedUpdate>(SpeedUpdate.Disabled)

    private val permissionManager = PermissionManager(context)

    private val provider: String? = locationManager.getBestProvider(
        Criteria()
            .apply { isSpeedRequired = true },
        false,
    )

    private var currentSpeed: Float? = null
        private set(value) {
            field = value
            sendSpeedUpdate()
        }

    var enabled: Boolean = false
        private set(value) {
            field = value
            sendSpeedUpdate()
        }

    var isGPSEnabled = false
        private set(value) {
            field = value
            sendSpeedUpdate()
        }

    private val locationListener = object : LocationListenerCompat {
        override fun onLocationChanged(location: Location) {
            currentSpeed = location.speed
        }

        override fun onProviderEnabled(provider: String) {
            isGPSEnabled = true
        }

        override fun onProviderDisabled(provider: String) {
            isGPSEnabled = false
            currentSpeed = null
        }
    }

    init {
        updateGPSState()
    }

    private fun updateGPSState() {
        isGPSEnabled = provider != null && locationManager.isProviderEnabled(provider)
    }

    fun toggle(state: Boolean) {
        if (state) {
            enable()
        } else {
            disable()
        }
    }

    @SuppressLint("MissingPermission")
    fun enable() {
        if (enabled) {
            return
        }
        updateGPSState()

        if (permissionManager.hasPermission() && provider != null) {
            locationManager.requestLocationUpdates(provider, 800, 0f, locationListener)
        }
        enabled = true
    }

    fun disable() {
        if (!enabled) {
            return
        }
        enabled = false
        updateGPSState()
        locationManager.removeUpdates(locationListener)
    }

    private fun sendSpeedUpdate() {
        val speed = currentSpeed
        _speedUpdates.value = when {
            !enabled -> SpeedUpdate.Disabled
            speed != null -> SpeedUpdate.SpeedChanged(speed)
            !isGPSEnabled -> SpeedUpdate.GPSDisabled
            else -> SpeedUpdate.SpeedUnavailable
        }
    }

    override fun destroy() {
        disable()
    }
}
