package ch.rmy.android.statusbar_tacho.icons

import android.content.Context
import android.content.res.Resources

class IconProvider(context: Context) {

    private val resources: Resources = context.resources
    private val packageName: String = context.packageName

    fun getIconForNumber(number: Int): Int {
        val iconName = String.format(RES_FORMAT, Math.max(0, Math.min(MAX_VALUE, number)))
        return resources.getIdentifier(iconName, RES_TYPE, packageName)
    }

    companion object {

        private val MAX_VALUE = 200
        private val RES_FORMAT = "icon%04d"
        private val RES_TYPE = "drawable"

    }

}
