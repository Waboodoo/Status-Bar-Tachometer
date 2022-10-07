package ch.rmy.android.statusbar_tacho.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import androidx.core.content.getSystemService
import ch.rmy.android.statusbar_tacho.utils.Destroyable
import ch.rmy.android.statusbar_tacho.utils.PermissionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SpeedWatcher(context: Context) : Destroyable {

    private val locationManager: LocationManager =
        context.getSystemService()!!

    val speedUpdates: StateFlow<SpeedUpdate>
        get() = _speedUpdates

    private val _speedUpdates = MutableStateFlow<SpeedUpdate>(SpeedUpdate.SpeedUnavailable)

    private val permissionManager = PermissionManager(context)

    private val provider: String?
    private var currentSpeed: Float? = null
    var enabled: Boolean = false
        private set

    var isGPSEnabled = false
        private set

    private val locationListener = object : SimpleLocationListener() {
        override fun onLocationChanged(location: Location) {
            sendSpeedUpdate(location.speed)
        }

        override fun onProviderEnabled(provider: String) {
            isGPSEnabled = true
            sendSpeedUpdate(currentSpeed)
        }

        override fun onProviderDisabled(provider: String) {
            isGPSEnabled = false
            sendSpeedUpdate(null)
        }
    }

    init {
        val criteria = Criteria()
        criteria.isSpeedRequired = true
        provider = locationManager.getBestProvider(criteria, false)

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
        enabled = true
        updateGPSState()

        if (permissionManager.hasPermission() && provider != null) {
            locationManager.requestLocationUpdates(provider, 800, 0f, locationListener)
            sendSpeedUpdate(null)
        }
    }

    fun disable() {
        if (!enabled) {
            return
        }
        enabled = false
        updateGPSState()
        if (permissionManager.hasPermission() && provider != null) {
            locationManager.removeUpdates(locationListener)
        }
    }

    private fun sendSpeedUpdate(speed: Float?) {
        if (speed == null || currentSpeed != speed) {
            currentSpeed = speed
            _speedUpdates.value = if (speed == null) {
                SpeedUpdate.SpeedUnavailable
            } else {
                SpeedUpdate.SpeedChanged(speed)
            }
        }
    }

    override fun destroy() {
        disable()
    }

    fun hasLocationPermission() = permissionManager.hasPermission()

}
