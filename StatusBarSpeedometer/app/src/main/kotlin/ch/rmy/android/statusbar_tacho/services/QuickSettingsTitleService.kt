package ch.rmy.android.statusbar_tacho.services

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import ch.rmy.android.statusbar_tacho.extensions.context
import ch.rmy.android.statusbar_tacho.utils.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class QuickSettingsTitleService : TileService() {

    private var coroutineScope: CoroutineScope? = null

    override fun onStartListening() {
        coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope?.launch {
            Settings.isRunningFlow.collect { isRunning ->
                updateState(isRunning)
            }
        }
    }

    override fun onStopListening() {
        coroutineScope?.cancel()
        coroutineScope = null
    }

    private fun updateState(running: Boolean) {
        qsTile.apply {
            state = if (running) {
                Tile.STATE_ACTIVE
            } else {
                Tile.STATE_INACTIVE
            }
            updateTile()
        }
    }

    override fun onClick() {
        val newState = !Settings.isRunning
        Settings.isRunning = newState
        SpeedometerService.setRunningState(context, newState)
    }

}