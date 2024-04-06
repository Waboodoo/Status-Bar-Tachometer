package ch.rmy.android.statusbar_tacho.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import android.widget.ToggleButton
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import ch.rmy.android.statusbar_tacho.R
import ch.rmy.android.statusbar_tacho.extensions.consume
import ch.rmy.android.statusbar_tacho.extensions.setOnItemSelectedListener
import ch.rmy.android.statusbar_tacho.location.SpeedUpdate
import ch.rmy.android.statusbar_tacho.location.SpeedWatcher
import ch.rmy.android.statusbar_tacho.services.SpeedometerService
import ch.rmy.android.statusbar_tacho.units.SpeedUnit
import ch.rmy.android.statusbar_tacho.utils.Dialogs
import ch.rmy.android.statusbar_tacho.utils.Links
import ch.rmy.android.statusbar_tacho.utils.PermissionManager
import ch.rmy.android.statusbar_tacho.utils.Settings
import ch.rmy.android.statusbar_tacho.utils.SpeedFormatter
import ch.rmy.android.statusbar_tacho.views.GaugeView
import kotlinx.coroutines.launch

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
                restartSpeedWatchers()
                updateViews()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupUnitSelector()
        setupCheckbox()

        findViewById<ToggleButton>(R.id.toggleButton).setOnCheckedChangeListener { _, isChecked -> toggleState(isChecked) }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                speedWatcher.speedUpdates.collect(::updateSpeedViews)
            }
        }

        Dialogs.showIntroMessage(context, settings)
    }

    private fun updateSpeedViews(speedUpdate: SpeedUpdate) {
        val convertedSpeed = unit.convertSpeed(
            when (speedUpdate) {
                is SpeedUpdate.SpeedChanged -> speedUpdate.speed
                else -> 0.0f
            }
        )
        findViewById<GaugeView>(R.id.speedGauge).value = convertedSpeed
        findViewById<TextView>(R.id.speedText).text = when (speedUpdate) {
            is SpeedUpdate.GPSDisabled -> getString(R.string.gps_disabled)
            is SpeedUpdate.SpeedChanged -> SpeedFormatter.formatSpeed(context, convertedSpeed)
            is SpeedUpdate.SpeedUnavailable,
            is SpeedUpdate.Disabled,
            -> IDLE_SPEED_PLACEHOLDER
        }
    }

    private fun restartSpeedWatchers() {
        if (speedWatcher.enabled) {
            speedWatcher.disable()
            speedWatcher.enable()
        }

        if (SpeedometerService.isRunning(context)) {
            SpeedometerService.restart(context)
        }
    }

    private fun updateViews() {
        val speedGauge = findViewById<GaugeView>(R.id.speedGauge)
        val toggleButton = findViewById<ToggleButton>(R.id.toggleButton)
        val keepOnWhileScreenOffCheckbox = findViewById<CheckBox>(R.id.keepOnWhileScreenOffCheckbox)
        val isRunning = settings.isRunning
        toggleButton.isChecked = isRunning
        speedGauge.maxValue = unit.maxValue.toFloat()
        speedGauge.markCount = unit.steps + 1
        keepOnWhileScreenOffCheckbox.isVisible = !isRunning
        keepScreenOn(isRunning)
    }

    private fun keepScreenOn(enabled: Boolean) {
        if (enabled) {
            window.addFlags(FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(FLAG_KEEP_SCREEN_ON)
        }
    }

    override fun onStart() {
        super.onStart()
        initState()
        updateViews()
    }

    private fun initState() {
        val state = SpeedometerService.isRunning(context)
        SpeedometerService.setRunningState(context, state)
        speedWatcher.toggle(state)
    }

    override fun onStop() {
        super.onStop()
        speedWatcher.disable()
    }

    private fun toggleState(state: Boolean) {
        if (state && !permissionManager.hasPermission()) {
            val toggleButton = findViewById<ToggleButton>(R.id.toggleButton)
            toggleButton.isChecked = false
            permissionManager.requestPermissions(this)
            return
        }

        settings.isRunning = state
        speedWatcher.toggle(state)
        SpeedometerService.setRunningState(context, state)
        updateViews()
    }

    private fun setupUnitSelector() {
        val unitNames = SpeedUnit.values().map { getText(it.nameRes) }
        val dataAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, unitNames)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val unitSpinner = findViewById<Spinner>(R.id.unitSpinner)
        unitSpinner.adapter = dataAdapter
        unitSpinner.setSelection(SpeedUnit.values().indexOf(unit))
        unitSpinner.setOnItemSelectedListener { position ->
            unit = SpeedUnit.values()[position]
        }
    }

    private fun setupCheckbox() {
        val keepOnWhileScreenOffCheckbox = findViewById<CheckBox>(R.id.keepOnWhileScreenOffCheckbox)
        keepOnWhileScreenOffCheckbox.isChecked = settings.shouldKeepUpdatingWhileScreenIsOff
        keepOnWhileScreenOffCheckbox.setOnCheckedChangeListener { _, checked ->
            settings.shouldKeepUpdatingWhileScreenIsOff = checked
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissionManager.wasGranted(grantResults)) {
            val toggleButton = findViewById<ToggleButton>(R.id.toggleButton)
            toggleButton.isChecked = true
        }
    }

    companion object {

        private const val IDLE_SPEED_PLACEHOLDER = "---"

    }

}
