package ch.rmy.android.statusbar_tacho.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import ch.rmy.android.statusbar_tacho.utils.Destroyer

abstract class BaseService : Service() {

    internal val destroyer = Destroyer()

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        destroyer.destroy()
    }

}
