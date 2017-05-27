package ch.rmy.android.statusbar_tacho.location

import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import ch.rmy.android.statusbar_tacho.utils.Destroyable
import ch.rmy.android.statusbar_tacho.utils.EventSource
import ch.rmy.android.statusbar_tacho.utils.PermissionManager

class SpeedWatcher(context: Context) : Destroyable {

    private val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val speedSource = EventSource<Float?>()

    private val permissionManager = PermissionManager(context)

    private val provider: String?
    var currentSpeed: Float? = null
        private set
    private var enabled: Boolean = false
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

        isGPSEnabled = provider != null && locationManager.isProviderEnabled(provider)
    }

    fun toggle(state: Boolean) {
        if (state) {
            enable()
        } else {
            disable()
        }
    }

    fun enable() {
        if (enabled) {
            return
        }
        enabled = true

        if (permissionManager.hasLocationPermission() && provider != null) {
            locationManager.requestLocationUpdates(provider, 800, 0f, locationListener)
            sendSpeedUpdate(null)
        }
    }

    fun disable() {
        if (!enabled) {
            return
        }
        enabled = false
        if (permissionManager.hasLocationPermission() && provider != null) {
            locationManager.removeUpdates(locationListener)
        }
    }

    private fun sendSpeedUpdate(speed: Float?) {
        if (speed == null || currentSpeed != speed) {
            currentSpeed = speed
            speedSource.notify(speed)
        }
    }

    override fun destroy() {
        disable()
    }

    fun hasLocationPermission() = permissionManager.hasLocationPermission()

}
