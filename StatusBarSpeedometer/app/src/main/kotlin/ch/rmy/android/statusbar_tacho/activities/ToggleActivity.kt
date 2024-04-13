package ch.rmy.android.statusbar_tacho.activities

import android.app.Activity
import android.os.Bundle
import ch.rmy.android.statusbar_tacho.extensions.context

import ch.rmy.android.statusbar_tacho.services.SpeedometerService
import ch.rmy.android.statusbar_tacho.utils.Settings

class ToggleActivity : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val newState = !Settings.isRunning
        SpeedometerService.setRunningState(context, newState)
        Settings.isRunning = newState
        finish()
    }
}
