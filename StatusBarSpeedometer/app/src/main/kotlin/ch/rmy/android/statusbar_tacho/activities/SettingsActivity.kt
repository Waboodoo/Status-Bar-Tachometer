package ch.rmy.android.statusbar_tacho.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ArrayAdapter
import ch.rmy.android.statusbar_tacho.R
import ch.rmy.android.statusbar_tacho.extensions.consume
import ch.rmy.android.statusbar_tacho.extensions.ownedBy
import ch.rmy.android.statusbar_tacho.location.SpeedUpdate
import ch.rmy.android.statusbar_tacho.location.SpeedWatcher
import ch.rmy.android.statusbar_tacho.services.SpeedometerService
import ch.rmy.android.statusbar_tacho.units.SpeedUnit
import ch.rmy.android.statusbar_tacho.utils.Dialogs
import ch.rmy.android.statusbar_tacho.utils.Links
import ch.rmy.android.statusbar_tacho.utils.PermissionManager
import ch.rmy.android.statusbar_tacho.utils.Settings
import ch.rmy.android.statusbar_tacho.utils.SimpleItemSelectedListener
import ch.rmy.android.statusbar_tacho.utils.SpeedFormatter
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() {

    override val navigateUpIcon = 0

    private val permissionManager: PermissionManager by lazy {
        PermissionManager(context)
    }
    private val speedWatcher: SpeedWatcher by lazy {
        destroyer.own(SpeedWatcher(context))
    }

    private val settings by lazy { Settings(context) }

    private var unit: SpeedUnit
        get() = settings.unit
        set(value) {
            if (value != settings.unit) {
                settings.unit = value
                onUnitChanged()

                if (SpeedometerService.isRunning(context)) {
                    SpeedometerService.restart(context)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        onUnitChanged()
        setupUnitSelector()

        toggleButton.setOnCheckedChangeListener { _, isChecked -> toggleState(isChecked) }

        speedWatcher.speedUpdates
            .subscribe { speedUpdate ->
                when (speedUpdate) {
                    is SpeedUpdate.SpeedChanged -> {
                        updateSpeedViews(speedUpdate.speed)
                    }
                    is SpeedUpdate.SpeedUnavailable -> {
                        updateSpeedViews(0f)
                    }
                }
            }
            .ownedBy(destroyer)

        Dialogs.showIntroMessage(context, settings)
    }

    private fun updateSpeedViews(speed: Float) {
        val convertedSpeed = unit.convertSpeed(speed)
        speedGauge.value = convertedSpeed
        speedText.text = when {
            !toggleButton.isChecked -> getString(R.string.idle_speed)
            !speedWatcher.isGPSEnabled -> getString(R.string.gps_disabled)
            !speedWatcher.hasLocationPermission() -> getString(R.string.permission_missing)
            else -> SpeedFormatter.formatSpeed(context, convertedSpeed)
        }
    }

    private fun onUnitChanged() {
        speedGauge.maxValue = unit.maxValue.toFloat()
        speedGauge.markCount = unit.steps + 1
        if (speedWatcher.enabled) {
            speedWatcher.disable()
            speedWatcher.enable()
        }
    }

    override fun onStart() {
        super.onStart()
        initState()
    }

    private fun initState() {
        val state = SpeedometerService.isRunning(context)
        SpeedometerService.setRunningState(context, state)
        toggleButton.isChecked = SpeedometerService.isRunning(context)
        speedWatcher.toggle(state)
    }

    override fun onStop() {
        super.onStop()
        speedWatcher.disable()
    }

    private fun toggleState(state: Boolean) {
        if (state && !permissionManager.hasLocationPermission()) {
            toggleButton.isChecked = false
            permissionManager.requestLocationPermission(this)
            return
        }

        if (state) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        speedWatcher.toggle(state)
        SpeedometerService.setRunningState(context, state)
        updateSpeedViews(0f)
    }

    private fun setupUnitSelector() {
        val unitNames = SpeedUnit.values().map { getText(it.nameRes) }
        val dataAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, unitNames)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        unitSpinner.adapter = dataAdapter
        unitSpinner.setSelection(SpeedUnit.values().indexOf(unit))
        unitSpinner.onItemSelectedListener = object : SimpleItemSelectedListener() {
            override fun onItemSelected(position: Int) {
                unit = SpeedUnit.values()[position]
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.action_github -> consume {
                Links.openGithub(context)
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (permissionManager.wasGranted(grantResults)) {
            toggleButton.isChecked = true
        }
    }

}
