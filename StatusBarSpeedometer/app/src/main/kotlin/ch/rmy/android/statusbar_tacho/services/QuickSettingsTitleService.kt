package ch.rmy.android.statusbar_tacho.services

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import ch.rmy.android.statusbar_tacho.extensions.context

@RequiresApi(Build.VERSION_CODES.N)
class QuickSettingsTitleService : TileService() {

    override fun onStartListening() {
        updateState(SpeedometerService.isRunning(context))
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
        updateState(!SpeedometerService.isRunning(context))
        SpeedometerService.toggleRunningState(context)
    }

}