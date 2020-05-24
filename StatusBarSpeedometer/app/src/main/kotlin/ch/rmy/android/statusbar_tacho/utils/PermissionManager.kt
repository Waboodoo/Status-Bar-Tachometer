package ch.rmy.android.statusbar_tacho.utils

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED

class PermissionManager(private val context: Context) {

    fun hasLocationPermission(): Boolean =
        PermissionChecker.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED

    fun requestLocationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(activity, arrayOf(ACCESS_FINE_LOCATION), 0)
    }

    fun wasGranted(grantResults: IntArray): Boolean =
        grantResults.singleOrNull() == PERMISSION_GRANTED

}
