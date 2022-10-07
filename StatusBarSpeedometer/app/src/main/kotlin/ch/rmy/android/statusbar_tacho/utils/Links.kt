package ch.rmy.android.statusbar_tacho.utils

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

object Links {

    private const val GITHUB_URL = "https://github.com/Waboodoo/Status-Bar-Tachometer"

    fun openGithub(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, GITHUB_URL.toUri())
        context.startActivity(intent)
    }

}
