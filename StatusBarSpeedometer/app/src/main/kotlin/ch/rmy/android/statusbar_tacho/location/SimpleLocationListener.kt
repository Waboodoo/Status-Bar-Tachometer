package ch.rmy.android.statusbar_tacho.location

import android.location.Location
import android.location.LocationListener
import android.os.Bundle

abstract class SimpleLocationListener : LocationListener {

    override fun onLocationChanged(location: Location) {

    }

    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onProviderDisabled(provider: String) {

    }

}
