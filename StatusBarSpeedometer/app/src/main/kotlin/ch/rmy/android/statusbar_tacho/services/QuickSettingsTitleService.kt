package ch.rmy.android.statusbar_tacho.services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import ch.rmy.android.statusbar_tacho.activities.ToggleActivity
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
        startIntent(Intent(context, ToggleActivity::class.java))
    }

    @Suppress("DEPRECATION")
    @SuppressLint("StartActivityAndCollapseDeprecated")
    private fun startIntent(intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE
            } else 0
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, flags)
            startActivityAndCollapse(pendingIntent)
        } else {
            startActivityAndCollapse(intent)
        }
    }

}