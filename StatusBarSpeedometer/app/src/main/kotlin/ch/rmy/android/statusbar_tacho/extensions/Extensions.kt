package ch.rmy.android.statusbar_tacho.extensions

import android.app.Service
import android.content.Context
import android.widget.Spinner
import ch.rmy.android.statusbar_tacho.utils.Destroyer
import ch.rmy.android.statusbar_tacho.utils.SimpleItemSelectedListener
import kotlinx.coroutines.Job

inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}

fun Job.ownedBy(destroyer: Destroyer) {
    destroyer.own {
        cancel()
    }
}

val Service.context: Context
    get() = this

fun Spinner.setOnItemSelectedListener(listener: (Int) -> Unit) {
    onItemSelectedListener = object : SimpleItemSelectedListener() {
        override fun onItemSelected(position: Int) {
            listener(position)
        }
    }
}