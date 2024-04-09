package ch.rmy.android.statusbar_tacho.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import ch.rmy.android.statusbar_tacho.R

class ShortcutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shortcut)

        findViewById<View>(R.id.btn_shortcut_ok).setOnClickListener { onClickButton() }
    }

    private fun onClickButton() {
        val name: Int
        val action: String

        when (findViewById<RadioGroup>(R.id.radio_shortcut).checkedRadioButtonId) {
            R.id.radio_shortcut_enable -> {
                name = R.string.shortcut_on
                action = SHORTCUT_ENABLE
            }
            R.id.radio_shortcut_disable -> {
                name = R.string.shortcut_off
                action = SHORTCUT_DISABLE
            }
            R.id.radio_shortcut_toggle -> {
                name = R.string.shortcut_toggle
                action = SHORTCUT_TOGGLE
            }
            else -> {
                finish()
                return
            }
        }

        setResult(Activity.RESULT_OK, createShortcutIntent(action, getString(name)))
        finish()
    }

    private fun createShortcutIntent(action: String, name: String): Intent {
        val icon = Intent.ShortcutIconResource.fromContext(this, R.mipmap.ic_launcher)

        val launchIntent = Intent(this, DummyActivity::class.java)
            .setPackage(packageName)
            .setAction(action)

        return Intent()
            .putExtra(Intent.EXTRA_SHORTCUT_INTENT, launchIntent)
            .putExtra(Intent.EXTRA_SHORTCUT_NAME, name)
            .putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon)
    }

    companion object {

        const val SHORTCUT_ENABLE = "ch.rmy.android.statusbar_tacho.enable"
        const val SHORTCUT_DISABLE = "ch.rmy.android.statusbar_tacho.disable"
        const val SHORTCUT_TOGGLE = "ch.rmy.android.statusbar_tacho.toggle"
    }

}
