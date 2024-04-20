package ch.rmy.android.statusbar_tacho.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import ch.rmy.android.statusbar_tacho.activities.SettingsActivity.Companion.EXTRA_ENABLE
import ch.rmy.android.statusbar_tacho.extensions.context

import ch.rmy.android.statusbar_tacho.services.SpeedometerService
import ch.rmy.android.statusbar_tacho.utils.PermissionManager
import ch.rmy.android.statusbar_tacho.utils.Settings

class ToggleActivity : Activity() {

    private val permissionManager: PermissionManager by lazy {
        PermissionManager(context)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (permissionManager.hasPermission()) {
            val newState = !Settings.isRunning
            SpeedometerService.setRunningState(context, newState)
            Settings.isRunning = newState
        } else {
            startActivity(
                Intent(this, SettingsActivity::class.java)
                    .putExtra(EXTRA_ENABLE, true)
            )
        }
        finish()
    }
}
