package ch.rmy.android.statusbar_tacho.activities

import android.app.Activity
import android.os.Bundle

import ch.rmy.android.statusbar_tacho.services.SpeedometerService

class DummyActivity : Activity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (intent.action) {
            ShortcutActivity.SHORTCUT_ENABLE -> {
                SpeedometerService.setRunningState(this, true)
            }
            ShortcutActivity.SHORTCUT_DISABLE -> {
                SpeedometerService.setRunningState(this, false)
            }
            ShortcutActivity.SHORTCUT_TOGGLE -> {
                SpeedometerService.setRunningState(this, !SpeedometerService.isRunning(this))
            }
        }

        finish()
    }

}
