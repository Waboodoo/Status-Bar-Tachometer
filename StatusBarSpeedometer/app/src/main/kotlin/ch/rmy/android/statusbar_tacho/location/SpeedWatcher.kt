package ch.rmy.android.statusbar_tacho.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import ch.rmy.android.statusbar_tacho.utils.Destroyable
import ch.rmy.android.statusbar_tacho.utils.PermissionManager
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class SpeedWatcher(context: Context) : Destroyable {

    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val speedUpdates: Observable<SpeedUpdate>
        get() = speedUpdatesSubject

    private val speedUpdatesSubject =
        BehaviorSubject.createDefault<SpeedUpdate>(SpeedUpdate.SpeedUnavailable)

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
        updateGPSState()
        if (permissionManager.hasLocationPermission() && provider != null) {
            locationManager.removeUpdates(locationListener)
        }
    }

    private fun sendSpeedUpdate(speed: Float?) {
        if (speed == null || currentSpeed != speed) {
            currentSpeed = speed
            speedUpdatesSubject.onNext(if (speed == null) {
                SpeedUpdate.SpeedUnavailable
            } else {
                SpeedUpdate.SpeedChanged(speed)
            })
        }
    }

    override fun destroy() {
        disable()
        speedUpdatesSubject.onComplete()
    }

    fun hasLocationPermission() = permissionManager.hasLocationPermission()

}
