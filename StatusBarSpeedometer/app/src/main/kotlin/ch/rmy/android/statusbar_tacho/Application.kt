package ch.rmy.android.statusbar_tacho

import android.app.Application
import ch.rmy.android.statusbar_tacho.utils.Settings

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        Settings.init(this)
    }
}