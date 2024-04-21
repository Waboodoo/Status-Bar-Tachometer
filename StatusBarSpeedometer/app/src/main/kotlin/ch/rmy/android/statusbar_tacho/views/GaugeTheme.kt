package ch.rmy.android.statusbar_tacho.views

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
data class GaugeTheme(
    val arcColor: Color,
    val needleColor: Color,
    val bigMarkColor: Color,
    val smallMarkColor: Color,
    val numberColor: Color,
    val backgroundColor: Color,
)

@Stable
data class GaugeThemeWrapper(
    val dark: GaugeTheme,
    val light: GaugeTheme,
)

@Composable
fun getGaugeTheme(themeId: ThemeId = ThemeId.DEFAULT): GaugeTheme =
    when (themeId) {
        ThemeId.DEFAULT -> defaultTheme
        ThemeId.BLUE -> blueTheme
        ThemeId.RED -> redTheme
        ThemeId.BLACK_AND_WHITE -> blackAndWhiteTheme
    }
        .run {
            if (isSystemInDarkTheme()) dark else light
        }

@Stable
private val defaultTheme = GaugeThemeWrapper(
    dark = GaugeTheme(
        arcColor = Color(0xFFFFFFFF),
        needleColor = Color(0xFFAA0000),
        bigMarkColor = Color(0xCCFFFFFF),
        smallMarkColor = Color(0xFF607D8B),
        numberColor = Color(0xFFFFFFFF),
        backgroundColor = Color(0xFF141B1E),
    ),
    light = GaugeTheme(
        arcColor = Color(0xFF607D8B),
        needleColor = Color(0xFFAA0000),
        bigMarkColor = Color(0xFF607D8B),
        smallMarkColor = Color(0xFF607D8B),
        numberColor = Color(0xFF141B1E),
        backgroundColor = Color(0xFFFAFAFA),
    )
)

@Stable
private val blueTheme = GaugeThemeWrapper(
    dark = GaugeTheme(
        arcColor = Color(0xFF5078B4),
        needleColor = Color(0xFF39A1D3),
        bigMarkColor = Color(0xFF12A7EE),
        smallMarkColor = Color(0xFF48B0E2),
        numberColor = Color(0xFFBAE0F0),
        backgroundColor = Color(0xFF141B1E),
    ),
    light = GaugeTheme(
        arcColor = Color(0xFF1A237E),
        needleColor = Color(0xFF1D8ABE),
        bigMarkColor = Color(0xFF12A7EE),
        smallMarkColor = Color(0xFF48B0E2),
        numberColor = Color(0xFF13242B),
        backgroundColor = Color(0xFFFAFAFA),
    )
)

@Stable
private val redTheme = GaugeThemeWrapper(
    dark = GaugeTheme(
        arcColor = Color(0xFFB13232),
        needleColor = Color(0xFFDA3636),
        bigMarkColor = Color(0xFFEE1212),
        smallMarkColor = Color(0xFFE24848),
        numberColor = Color(0xFFF5F5F5),
        backgroundColor = Color(0xFF141B1E),
    ),
    light = GaugeTheme(
        arcColor = Color(0xFF7E1A1A),
        needleColor = Color(0xFFBE1D1D),
        bigMarkColor = Color(0xFFEE1212),
        smallMarkColor = Color(0xFFE24848),
        numberColor = Color(0xFF2B1313),
        backgroundColor = Color(0xFFFAFAFA),
    )
)

@Stable
private val blackAndWhiteTheme = GaugeThemeWrapper(
    dark = GaugeTheme(
        arcColor = Color.White,
        needleColor = Color.White,
        bigMarkColor = Color.White,
        smallMarkColor = Color.White,
        numberColor = Color.White,
        backgroundColor = Color.Black,
    ),
    light = GaugeTheme(
        arcColor = Color.Black,
        needleColor = Color.Black,
        bigMarkColor = Color.Black,
        smallMarkColor = Color.Black,
        numberColor = Color.Black,
        backgroundColor = Color.White,
    )
)