package ch.rmy.android.statusbar_tacho.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.SystemClock
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

    private var currentSpeed: Float? = null
        private set(value) {
            field = value
            sendSpeedUpdate()
        }
    private var lastGpsUpdate = 0L

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

    private val gpsLocationListener = object : LocationListenerCompat {
        override fun onLocationChanged(location: Location) {
            currentSpeed = location.speed
            lastGpsUpdate = SystemClock.elapsedRealtime()
        }

        override fun onProviderEnabled(provider: String) {
            isGPSEnabled = true
        }

        override fun onProviderDisabled(provider: String) {
            isGPSEnabled = false
            currentSpeed = null
        }
    }

    private val fusedLocationListener = LocationListenerCompat { location ->
        if (currentSpeed == null || SystemClock.elapsedRealtime() - lastGpsUpdate > 10000) {
            currentSpeed = location.speed
        }
    }

    init {
        updateGPSState()
    }

    private fun updateGPSState() {
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
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

        if (permissionManager.hasPermission()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 800, 0f, gpsLocationListener)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 800, 0f, fusedLocationListener)
            }
        }
        enabled = true
    }

    fun disable() {
        if (!enabled) {
            return
        }
        enabled = false
        updateGPSState()
        locationManager.removeUpdates(gpsLocationListener)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            locationManager.removeUpdates(fusedLocationListener)
        }
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
