package ch.rmy.android.statusbar_tacho.icons

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import androidx.annotation.DrawableRes
import java.util.Locale

class IconProvider(context: Context) {

    private val resources: Resources = context.resources
    private val packageName: String = context.packageName

    @SuppressLint("DiscouragedApi")
    @DrawableRes
    fun getIconForNumber(number: Int): Int =
        String.format(Locale.US, RES_FORMAT, number.coerceIn(0, MAX_VALUE))
            .let { iconName ->
                resources.getIdentifier(iconName, RES_TYPE, packageName)
            }

    companion object {

        private const val MAX_VALUE = 999
        private const val RES_FORMAT = "icon%03d"
        private const val RES_TYPE = "drawable"

    }

}
