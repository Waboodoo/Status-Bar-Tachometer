package ch.rmy.android.statusbar_tacho.extensions

import android.content.SharedPreferences
import ch.rmy.android.statusbar_tacho.utils.Destroyer
import io.reactivex.disposables.Disposable

inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}

fun SharedPreferences.edit(block: SharedPreferences.Editor.() -> Unit) {
    edit().apply {
        block()
        apply()
    }
}

fun Disposable.ownedBy(destroyer: Destroyer) {
    destroyer.own {
        dispose()
    }
}