package ch.rmy.android.statusbar_tacho.utils

import android.view.View
import android.widget.AdapterView

abstract class SimpleItemSelectedListener : AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        onItemSelected(position)
    }

    open fun onItemSelected(position: Int) {

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}
