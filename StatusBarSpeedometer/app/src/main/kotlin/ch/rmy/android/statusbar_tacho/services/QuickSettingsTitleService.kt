package ch.rmy.android.statusbar_tacho.services

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import ch.rmy.android.statusbar_tacho.extensions.context
import ch.rmy.android.statusbar_tacho.utils.Settings

@RequiresApi(Build.VERSION_CODES.N)
class QuickSettingsTitleService : TileService() {

    override fun onStartListening() {
        updateState(Settings.isRunning)
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
        updateState(!Settings.isRunning)
        SpeedometerService.toggleRunningState(context)
    }

}