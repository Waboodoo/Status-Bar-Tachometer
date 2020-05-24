package ch.rmy.android.statusbar_tacho.icons

import android.content.Context
import android.content.res.Resources
import androidx.annotation.DrawableRes
import kotlin.math.max
import kotlin.math.min

class IconProvider(context: Context) {

    private val resources: Resources = context.resources
    private val packageName: String = context.packageName

    @DrawableRes
    fun getIconForNumber(number: Int): Int =
        String.format(RES_FORMAT, max(0, min(MAX_VALUE, number)))
            .let { iconName ->
                resources.getIdentifier(iconName, RES_TYPE, packageName)
            }

    companion object {

        private const val MAX_VALUE = 200
        private const val RES_FORMAT = "icon%04d"
        private const val RES_TYPE = "drawable"

    }

}
