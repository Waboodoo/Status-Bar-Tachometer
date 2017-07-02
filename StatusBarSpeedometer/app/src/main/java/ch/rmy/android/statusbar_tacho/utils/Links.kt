package ch.rmy.android.statusbar_tacho.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

object Links {

    private val GITHUB_URL = "https://github.com/Waboodoo/Status-Bar-Tachometer"

    fun openGithub(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL))
        context.startActivity(intent)
    }

}
