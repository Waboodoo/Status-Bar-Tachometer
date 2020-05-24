package ch.rmy.android.statusbar_tacho.activities

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import ch.rmy.android.statusbar_tacho.R
import ch.rmy.android.statusbar_tacho.extensions.consume
import ch.rmy.android.statusbar_tacho.utils.Destroyer

abstract class BaseActivity : AppCompatActivity() {

    internal val destroyer = Destroyer()

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        updateStatusBarColor()
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            if (navigateUpIcon != 0) {
                enableNavigateUpButton(navigateUpIcon)
            }
        }
    }

    internal val context: Context
        get() = this

    internal open val navigateUpIcon: Int
        get() = R.drawable.up_arrow

    private fun enableNavigateUpButton(iconResource: Int) {
        supportActionBar?.let { actionBar ->
            actionBar.setDisplayHomeAsUpEnabled(true)
            ContextCompat.getDrawable(context, iconResource)
                ?.let { upArrow ->
                    DrawableCompat.setTint(upArrow, Color.WHITE)
                    actionBar.setHomeAsUpIndicator(upArrow)
                }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> consume {
                onBackPressed()
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun updateStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                statusBarColor = ContextCompat.getColor(context, R.color.primary_dark)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyer.destroy()
    }
}