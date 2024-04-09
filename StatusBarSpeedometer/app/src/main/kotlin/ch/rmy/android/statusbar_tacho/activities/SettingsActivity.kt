package ch.rmy.android.statusbar_tacho.activities

import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import ch.rmy.android.statusbar_tacho.R
import ch.rmy.android.statusbar_tacho.extensions.context
import ch.rmy.android.statusbar_tacho.location.SpeedUpdate
import ch.rmy.android.statusbar_tacho.location.SpeedWatcher
import ch.rmy.android.statusbar_tacho.services.SpeedometerService
import ch.rmy.android.statusbar_tacho.utils.AppTheme
import ch.rmy.android.statusbar_tacho.utils.PermissionManager
import ch.rmy.android.statusbar_tacho.utils.Settings
import ch.rmy.android.statusbar_tacho.utils.SpeedFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private val permissionManager: PermissionManager by lazy {
        PermissionManager(context)
    }
    private val speedWatcher: SpeedWatcher by lazy {
        SpeedWatcher(context)
    }

    private val settings: Settings
        get() = Settings

    private val _speedUnit = MutableStateFlow(settings.unit)
    private val _isRunning = MutableStateFlow(settings.isRunning)
    private val _runWhenScreenOff = MutableStateFlow(settings.shouldKeepUpdatingWhileScreenIsOff)
    private val _speedUpdate = MutableStateFlow<SpeedUpdate>(SpeedUpdate.Disabled)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                speedWatcher.speedUpdates.collectLatest {
                    _speedUpdate.value = it
                }
            }
        }

        lifecycleScope.launch {
            _speedUnit.drop(1).collectLatest {
                settings.unit = it
            }
        }

        lifecycleScope.launch {
            _isRunning.collectLatest { isRunning ->
                settings.isRunning = isRunning
                keepScreenOn(isRunning)
                speedWatcher.toggle(isRunning)
                SpeedometerService.setRunningState(context, isRunning)
            }
        }

        setContent {
            val isRunning by _isRunning.collectAsStateWithLifecycle()
            val speedUnit by _speedUnit.collectAsStateWithLifecycle()
            val speedUpdate by _speedUpdate.collectAsStateWithLifecycle()
            val runWhenScreenOff by _runWhenScreenOff.collectAsStateWithLifecycle()

            var isFirstRun by remember {
                mutableStateOf(settings.isFirstRun)
            }

            var settingsVisible by rememberSaveable {
                mutableStateOf(false)
            }

            val speed by remember {
                derivedStateOf {
                    speedUnit.convertSpeed((speedUpdate as? SpeedUpdate.SpeedChanged)?.speed ?: 0.0f)
                }
            }

            AppTheme {
                MainScreen(
                    gaugeValue = speed,
                    gaugeMaxValue = speedUnit.maxValue.toFloat(),
                    gaugeMarkCount = speedUnit.steps + 1,
                    speedLabel = when (speedUpdate) {
                        is SpeedUpdate.GPSDisabled -> stringResource(R.string.gps_disabled)
                        is SpeedUpdate.SpeedChanged -> SpeedFormatter.formatSpeed(context, speed)
                        is SpeedUpdate.SpeedUnavailable,
                        is SpeedUpdate.Disabled,
                        -> IDLE_SPEED_PLACEHOLDER
                    },
                    isRunning = isRunning,
                    onClicked = {
                        if (!isRunning && !permissionManager.hasPermission()) {
                            permissionManager.requestPermissions(this@SettingsActivity)
                        } else {
                            _isRunning.value = !isRunning
                        }
                        settingsVisible = false
                    },
                    onSettingsClicked = {
                        settingsVisible = true
                    },
                )

                if (isFirstRun) {
                    WelcomeDialog(
                        onDismissRequest = {
                            isFirstRun = false
                            settings.isFirstRun = false
                        }
                    )
                } else if (settingsVisible) {
                    SettingsDialog(
                        speedUnit = speedUnit,
                        runWhenScreenOff = runWhenScreenOff,
                        onSpeedUnitChanged = {
                            _speedUnit.value = it
                            Settings.unit = it
                        },
                        onRunWhenScreenOffChanged = {
                            _runWhenScreenOff.value = it
                            Settings.shouldKeepUpdatingWhileScreenIsOff = it
                        },
                        onDismissRequest = {
                            settingsVisible = false
                            _speedUnit.value = settings.unit
                        }
                    )
                }
            }
        }
    }

    private fun keepScreenOn(enabled: Boolean) {
        with(window) {
            if (enabled) {
                addFlags(FLAG_KEEP_SCREEN_ON)
            } else {
                clearFlags(FLAG_KEEP_SCREEN_ON)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        initState()
    }

    private fun initState() {
        val state = Settings.isRunning
        SpeedometerService.setRunningState(context, state)
        speedWatcher.toggle(state)
    }

    override fun onStop() {
        super.onStop()
        speedWatcher.disable()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        _isRunning.value = permissionManager.wasGranted(grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()
        speedWatcher.destroy()
    }

    companion object {

        private const val IDLE_SPEED_PLACEHOLDER = "---"

    }

}
