package ch.rmy.android.statusbar_tacho.activities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ch.rmy.android.statusbar_tacho.location.SpeedWatcher

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    val speedWatcher: SpeedWatcher by lazy {
        SpeedWatcher(application.applicationContext)
    }

    override fun onCleared() {
        super.onCleared()
        speedWatcher.destroy()
    }
}