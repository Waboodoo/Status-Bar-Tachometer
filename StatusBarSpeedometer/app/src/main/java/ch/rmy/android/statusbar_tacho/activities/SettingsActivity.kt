package ch.rmy.android.statusbar_tacho.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import ch.rmy.android.statusbar_tacho.R
import ch.rmy.android.statusbar_tacho.services.SpeedometerService
import ch.rmy.android.statusbar_tacho.units.Units
import ch.rmy.android.statusbar_tacho.utils.Links
import ch.rmy.android.statusbar_tacho.utils.PermissionManager
import ch.rmy.android.statusbar_tacho.utils.Settings
import ch.rmy.android.statusbar_tacho.utils.SimpleItemSelectedListener
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() {

    override val navigateUpIcon = 0

    private var permissionManager: PermissionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settings = Settings(context)
        setupUnitSelector(settings)

        SpeedometerService.setRunningState(this, SpeedometerService.isRunning(this))

        permissionManager = PermissionManager(context)
        toggleButton.setOnCheckedChangeListener { _, isChecked -> toggleState(isChecked) }
    }

    internal fun toggleState(state: Boolean) {
        if (state && !permissionManager!!.hasLocationPermission()) {
            toggleButton.isChecked = false
            permissionManager!!.requestLocationPermission(this)
            return
        }

        SpeedometerService.setRunningState(context, state)
    }

    override fun onStart() {
        super.onStart()
        toggleButton.isChecked = SpeedometerService.isRunning(context)
    }

    internal fun setupUnitSelector(settings: Settings) {
        val unitNames = Units.UNITS.map { getText(it.nameRes) }
        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, unitNames)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        unitSpinner.adapter = dataAdapter
        unitSpinner.onItemSelectedListener = object : SimpleItemSelectedListener() {
            override fun onItemSelected(position: Int) {
                settings.unit = Units.UNITS[position]

                if (SpeedometerService.isRunning(context)) {
                    SpeedometerService.restart(context)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_github -> {
                Links.openGithub(context)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (permissionManager!!.wasGranted(grantResults)) {
            toggleButton.isChecked = true
        }
    }

    companion object {

        private val REQUEST_PERMISSION = 1

    }
}
