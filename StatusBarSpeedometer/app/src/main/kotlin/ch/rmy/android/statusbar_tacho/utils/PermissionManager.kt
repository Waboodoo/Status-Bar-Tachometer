package ch.rmy.android.statusbar_tacho.utils

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED

class PermissionManager(private val context: Context) {

    fun hasPermission(): Boolean =
        PermissionChecker.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED

    fun requestPermissions(activity: Activity) {
        val permissions =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(ACCESS_FINE_LOCATION, POST_NOTIFICATIONS)
            } else {
                arrayOf(ACCESS_FINE_LOCATION)
            }
        ActivityCompat.requestPermissions(activity, permissions, 0)
    }

    fun wasGranted(grantResults: IntArray): Boolean =
        grantResults.all { it == PERMISSION_GRANTED }

}
