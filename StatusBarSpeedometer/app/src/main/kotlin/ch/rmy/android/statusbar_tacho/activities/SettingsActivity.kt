package ch.rmy.android.statusbar_tacho.activities

import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import ch.rmy.android.statusbar_tacho.R
import ch.rmy.android.statusbar_tacho.extensions.context
import ch.rmy.android.statusbar_tacho.location.SpeedState
import ch.rmy.android.statusbar_tacho.services.SpeedometerService
import ch.rmy.android.statusbar_tacho.units.SpeedUnit
import ch.rmy.android.statusbar_tacho.utils.AppTheme
import ch.rmy.android.statusbar_tacho.utils.PermissionManager
import ch.rmy.android.statusbar_tacho.utils.Settings
import ch.rmy.android.statusbar_tacho.utils.SpeedFormatter
import ch.rmy.android.statusbar_tacho.views.GaugeScale
import ch.rmy.android.statusbar_tacho.views.getGaugeTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private val permissionManager: PermissionManager by lazy {
        PermissionManager(context)
    }

    private val viewModel by viewModels<SettingsViewModel>()
    val speedWatcher
        get() = viewModel.speedWatcher

    private val settings: Settings
        get() = Settings

    private val _speedUnit = settings.unitFlow
    private val _isRunning = settings.isRunningFlow
    private val _runWhenScreenOff = MutableStateFlow(settings.shouldKeepUpdatingWhileScreenIsOff)
    private val _speedState = MutableStateFlow<SpeedState>(SpeedState.Disabled)
    private val _themeId = settings.themeIdFlow
    private val _gaugeScale = settings.gaugeScaleFlow
    private val _showUnit = settings.showUnitFlow
    private val _permissionGranted = settings.permissionGrantedFlow

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (intent.getBooleanExtra(EXTRA_ENABLE, false) && !permissionManager.hasPermission()) {
            permissionManager.requestPermissions(this@SettingsActivity)
            intent.removeExtra(EXTRA_ENABLE)
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                speedWatcher.speedState.collectLatest {
                    _speedState.value = it
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
                keepScreenOn(isRunning)
                speedWatcher.toggle(isRunning)
                SpeedometerService.setRunningState(context, isRunning)
            }
        }

        setContent {
            val isRunning by _isRunning.collectAsStateWithLifecycle()
            val speedUnit by _speedUnit.collectAsStateWithLifecycle()
            val speedUpdate by _speedState.collectAsStateWithLifecycle()
            val runWhenScreenOff by _runWhenScreenOff.collectAsStateWithLifecycle()
            val themeId by _themeId.collectAsStateWithLifecycle()
            val gaugeScale by _gaugeScale.collectAsStateWithLifecycle()
            val showUnit by _showUnit.collectAsStateWithLifecycle()
            val permissionGranted by _permissionGranted.collectAsStateWithLifecycle()

            var modal by remember {
                mutableStateOf(if (settings.isFirstRun) Modal.WELCOME else null)
            }
            var showSettings by rememberSaveable {
                mutableStateOf(false)
            }

            val speed by remember {
                derivedStateOf {
                    speedUnit.convertSpeed((speedUpdate as? SpeedState.SpeedChanged)?.speed ?: 0.0f)
                }
            }

            var gaugeScaleFactorIndex by rememberSaveable {
                mutableIntStateOf(0)
            }
            val gaugeMaxValue by remember(gaugeScale, speedUnit, gaugeScaleFactorIndex) {
                derivedStateOf {
                    getGaugeMaxValue(gaugeScale, speedUnit, gaugeScaleFactorIndex)
                }
            }

            BackHandler(showSettings) {
                showSettings = false
            }

            if (gaugeScale == GaugeScale.DYNAMIC) {
                LaunchedEffect(speed) {
                    var gaugeMaxValue = gaugeMaxValue
                    if (speed == 0f) {
                        gaugeScaleFactorIndex = 0
                    } else {
                        var newGaugeScaleFactorIndex = gaugeScaleFactorIndex
                        while (speed > gaugeMaxValue * 0.9f) {
                            newGaugeScaleFactorIndex = (newGaugeScaleFactorIndex + 1)
                            if (newGaugeScaleFactorIndex >= GaugeScale.FACTORS.lastIndex) {
                                newGaugeScaleFactorIndex = GaugeScale.FACTORS.lastIndex
                                break
                            }
                            gaugeMaxValue = getGaugeMaxValue(gaugeScale, speedUnit, newGaugeScaleFactorIndex)
                        }
                        while (speed < gaugeMaxValue * 0.2f) {
                            newGaugeScaleFactorIndex -= 1
                            if (newGaugeScaleFactorIndex <= 0) {
                                newGaugeScaleFactorIndex = 0
                                break
                            }
                            gaugeMaxValue = getGaugeMaxValue(gaugeScale, speedUnit, newGaugeScaleFactorIndex)
                        }
                        if (newGaugeScaleFactorIndex != gaugeScaleFactorIndex) {
                            gaugeScaleFactorIndex = newGaugeScaleFactorIndex
                        }
                    }
                }
            }

            val gaugeTheme = getGaugeTheme(themeId)
            val menuAlpha by animateFloatAsState(
                targetValue = if (isRunning || showSettings) 0f else 1f,
            )
            val closeSettingsAlpha by animateFloatAsState(
                targetValue = if (showSettings) 1f else 0f,
            )

            AppTheme {
                Scaffold(
                    modifier = Modifier
                        .imePadding()
                        .statusBarsPadding(),
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(gaugeTheme.backgroundColor),
                            contentAlignment = Alignment.Center,
                        ) {
                            MainScreen(
                                gaugeValue = speed,
                                gaugeMaxValue = gaugeMaxValue,
                                gaugeMarkCount = speedUnit.steps + 1,
                                gaugeTheme = gaugeTheme,
                                speedLabel = when (speedUpdate) {
                                    is SpeedState.GPSDisabled -> stringResource(R.string.gps_disabled)
                                    is SpeedState.SpeedChanged -> SpeedFormatter.formatSpeed(context, speed, if (showUnit) speedUnit else null)
                                    is SpeedState.SpeedUnavailable,
                                    is SpeedState.Disabled,
                                        -> if (permissionGranted == false) {
                                        stringResource(R.string.permission_not_granted)
                                    } else {
                                        IDLE_SPEED_PLACEHOLDER
                                    }
                                },
                                isRunning = isRunning,
                                isInSettings = showSettings,
                                onClicked = {
                                    if (!isRunning && !permissionManager.hasPermission()) {
                                        permissionManager.requestPermissions(this@SettingsActivity)
                                    } else {
                                        settings.isRunning = !isRunning
                                    }
                                    modal = null
                                },
                            )

                            if (menuAlpha > 0f) {
                                MainMenu(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .alpha(menuAlpha),
                                    onSettingsClicked = {
                                        showSettings = true
                                    },
                                    onTopSpeedClicked = {
                                        modal = Modal.TOP_SPEED
                                    },
                                )
                            }

                            if (closeSettingsAlpha > 0f) {
                                IconButton(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .alpha(closeSettingsAlpha)
                                        .padding(8.dp),
                                    onClick = {
                                        showSettings = false
                                    },
                                ) {
                                    Icon(
                                        painterResource(R.drawable.outline_arrow_back_24),
                                        contentDescription = stringResource(R.string.button_close),
                                        tint = MaterialTheme.colorScheme.onBackground,
                                    )
                                }
                            }
                        }

                        if (showSettings) {
                            SettingsScreen(
                                modifier = Modifier
                                    .padding(horizontal = 32.dp, vertical = 16.dp)
                                    .weight(2f)
                                    .verticalScroll(rememberScrollState()),
                                speedUnit = speedUnit,
                                themeId = themeId,
                                gaugeScale = gaugeScale,
                                showUnit = showUnit,
                                runWhenScreenOff = runWhenScreenOff,
                                onSpeedUnitChanged = {
                                    settings.unit = it
                                },
                                onThemeIdChanged = {
                                    settings.themeId = it
                                },
                                onGaugeScaleChanged = {
                                    settings.gaugeScale = it
                                },
                                onShowUnitChanged = {
                                    settings.showUnit = it
                                },
                                onRunWhenScreenOffChanged = {
                                    _runWhenScreenOff.value = it
                                    Settings.shouldKeepUpdatingWhileScreenIsOff = it
                                },
                            )
                        }
                    }
                }

                when (modal) {
                    Modal.WELCOME -> {
                        WelcomeDialog(
                            onDismissRequest = {
                                modal = null
                                settings.isFirstRun = false
                            }
                        )
                    }

                    Modal.TOP_SPEED -> {
                        TopSpeedDialog(
                            onDismissRequest = {
                                modal = null
                            }
                        )
                    }

                    null -> Unit
                }
            }
        }
    }

    @Stable
    private fun getGaugeMaxValue(gaugeScale: GaugeScale, speedUnit: SpeedUnit, gaugeScaleFactorIndex: Int): Float =
        speedUnit.maxValue / (gaugeScale.factor ?: GaugeScale.FACTORS[gaugeScaleFactorIndex])

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        settings.isRunning = permissionManager.wasGranted(grantResults)
        settings.hasPermission = permissionManager.hasPermission()
    }

    companion object {

        private const val IDLE_SPEED_PLACEHOLDER = "---"
        const val EXTRA_ENABLE = "enable"

    }

}
