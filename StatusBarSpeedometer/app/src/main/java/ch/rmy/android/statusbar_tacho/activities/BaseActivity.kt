package ch.rmy.android.statusbar_tacho.activities

import android.content.Context
import android.graphics.PorterDuff
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.WindowManager
import ch.rmy.android.statusbar_tacho.R
import ch.rmy.android.statusbar_tacho.utils.Destroyer

abstract class BaseActivity : AppCompatActivity() {

    internal val destroyer = Destroyer()

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        val toolbar = findViewById(R.id.toolbar) as Toolbar?
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
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            val upArrow = resources.getDrawable(iconResource)
            if (upArrow != null) {
                upArrow.setColorFilter(resources.getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP)
                actionBar.setHomeAsUpIndicator(upArrow)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = resources.getColor(R.color.primary_dark)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyer.destroy()
    }
}