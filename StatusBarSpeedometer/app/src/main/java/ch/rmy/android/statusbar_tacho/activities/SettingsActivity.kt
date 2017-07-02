package ch.rmy.android.statusbar_tacho.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ArrayAdapter
import ch.rmy.android.statusbar_tacho.R
import ch.rmy.android.statusbar_tacho.location.SpeedWatcher
import ch.rmy.android.statusbar_tacho.services.SpeedometerService
import ch.rmy.android.statusbar_tacho.units.Unit
import ch.rmy.android.statusbar_tacho.units.Units
import ch.rmy.android.statusbar_tacho.utils.*
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() {

    override val navigateUpIcon = 0

    private var permissionManager: PermissionManager? = null
    private var speedWatcher: SpeedWatcher? = null
    private var unit: Unit? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settings = Settings(context)
        unit = settings.unit
        onUnitChanged()
        setupUnitSelector(settings)

        speedWatcher = destroyer.own(SpeedWatcher(context))
        permissionManager = PermissionManager(context)

        toggleButton.setOnCheckedChangeListener { _, isChecked -> toggleState(isChecked) }
        speedWatcher!!.speedSource.bind(object : EventSource.Observer<Float?> {
            override fun on(currentSpeed: Float?) {
                if (currentSpeed == null) {
                    updateSpeedViews(0f)
                } else {
                    updateSpeedViews(currentSpeed)
                }
            }
        }, speedWatcher!!.currentSpeed)

        Dialogs.showIntroMessage(context, settings)
    }

    private fun updateSpeedViews(speed: Float) {
        val convertedSpeed = unit!!.convertSpeed(speed)
        speedGauge.value = convertedSpeed
        speedText.text = if (!toggleButton.isChecked) {
            getString(R.string.idle_speed)
        } else if (!speedWatcher!!.isGPSEnabled) {
            getString(R.string.gps_disabled)
        } else if (!speedWatcher!!.hasLocationPermission()) {
            getString(R.string.permission_missing)
        } else {
            getString(
                    R.string.speed_format_without_unit,
                    convertedSpeed
            )
        }
    }

    private fun onUnitChanged() {
        speedGauge.maxValue = unit!!.maxValue.toFloat()
        speedGauge.markCount = unit!!.steps + 1
    }

    override fun onStart() {
        super.onStart()
        initState()
    }

    private fun initState() {
        val state = SpeedometerService.isRunning(context)
        SpeedometerService.setRunningState(context, state)
        toggleButton.isChecked = SpeedometerService.isRunning(context)
        speedWatcher!!.toggle(state)
    }

    override fun onStop() {
        super.onStop()
        speedWatcher!!.disable()
    }

    private fun toggleState(state: Boolean) {
        if (state && !permissionManager!!.hasLocationPermission()) {
            toggleButton.isChecked = false
            permissionManager!!.requestLocationPermission(this)
            return
        }

        if (state) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        speedWatcher!!.toggle(state)
        SpeedometerService.setRunningState(context, state)
        updateSpeedViews(0f)
    }

    private fun setupUnitSelector(settings: Settings) {
        val unitNames = Units.UNITS.map { getText(it.nameRes) }
        val dataAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, unitNames)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        unitSpinner.adapter = dataAdapter
        unitSpinner.onItemSelectedListener = object : SimpleItemSelectedListener() {
            override fun onItemSelected(position: Int) {
                unit = Units.UNITS[position]
                settings.unit = unit as Unit
                onUnitChanged()

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

}
