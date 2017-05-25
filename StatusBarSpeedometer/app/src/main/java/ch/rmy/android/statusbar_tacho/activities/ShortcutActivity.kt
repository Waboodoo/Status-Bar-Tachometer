package ch.rmy.android.statusbar_tacho.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import ch.rmy.android.statusbar_tacho.R
import kotlinx.android.synthetic.main.activity_shortcut.*

class ShortcutActivity : BaseActivity() {

    internal var choice = R.id.radio_shortcut_enable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shortcut)

        radio_shortcut.setOnCheckedChangeListener { _, checkedId -> choice = checkedId }
        btn_shortcut_ok.setOnClickListener { _ -> onClickButton() }
    }

    internal fun onClickButton() {
        val name: Int
        val action: String
        if (choice == R.id.radio_shortcut_enable) {
            name = R.string.shortcut_on
            action = SHORTCUT_ENABLE
        } else if (choice == R.id.radio_shortcut_disable) {
            name = R.string.shortcut_off
            action = SHORTCUT_DISABLE
        } else {
            name = R.string.shortcut_toggle
            action = SHORTCUT_TOGGLE
        }

        val icon = Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher)

        val intent = Intent()

        val launchIntent = Intent(this, DummyActivity::class.java)
        launchIntent.`package` = packageName
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launchIntent)
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getText(name))
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon)
        launchIntent.action = action

        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    companion object {

        val SHORTCUT_ENABLE = "ch.rmy.android.statusbar_tacho.enable"
        val SHORTCUT_DISABLE = "ch.rmy.android.statusbar_tacho.disable"
        val SHORTCUT_TOGGLE = "ch.rmy.android.statusbar_tacho.toggle"
    }

}
