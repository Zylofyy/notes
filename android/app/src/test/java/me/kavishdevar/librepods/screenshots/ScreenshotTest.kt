/*
    LibrePods - AirPods liberated from Apple’s ecosystem
    Copyright (C) 2025 LibrePods contributors

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package me.kavishdevar.librepods.screenshots

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import me.kavishdevar.librepods.presentation.components.AppScaffold
import me.kavishdevar.librepods.presentation.screens.AirPodsSettingsScreen
import me.kavishdevar.librepods.presentation.theme.LibrePodsTheme
import me.kavishdevar.librepods.presentation.viewmodel.AirPodsUiState
import me.kavishdevar.librepods.presentation.viewmodel.demoState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/**
 * Renders the M3E UI on the JVM (Robolectric native graphics) and writes PNGs to
 * app/build/screenshots. Run with:
 *   ./gradlew :app:testFossDebugUnitTest --tests '*ScreenshotTest*'
 * Uses the LibrePods fallback palette, since dynamic color needs a real wallpaper.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36], application = Application::class, qualifiers = "w412dp-h915dp-xhdpi")
class ScreenshotTest {

    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    private fun capture(name: String, dark: Boolean = false, content: @Composable () -> Unit) {
        compose.setContent {
            LibrePodsTheme(darkTheme = dark, dynamicColor = false) { content() }
        }
        compose.mainClock.advanceTimeBy(2_000)
        compose.waitForIdle()
        // Draw the window in software; captureToImage() waits for a hardware frame Robolectric never produces.
        val view = compose.activity.window.decorView
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        view.draw(Canvas(bitmap))
        val dir = File("build/screenshots").apply { mkdirs() }
        File(dir, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    @Composable
    private fun Home(state: AirPodsUiState) {
        AppScaffold(
            title = if (state.isLocallyConnected) state.deviceName else "LibrePods",
            subtitle = if (state.isLocallyConnected) "Connected" else null,
            actions = {
                FilledTonalIconButton(onClick = {}, shapes = IconButtonDefaults.shapes()) {
                    Icon(Icons.Rounded.Settings, contentDescription = null)
                }
            }
        ) {
            AirPodsSettingsScreen(
                state = state,
                setControlCommandInt = { _, _ -> },
                setControlCommandBoolean = { _, _ -> },
                setControlCommandByte = { _, _ -> },
                setATTCharacteristicValue = { _, _ -> },
                onAutomaticEarDetectionChanged = {},
                onAutomaticConnectionChanged = {},
                setDynamicEndOfCharge = {},
                setOffListeningMode = {},
                disconnect = {},
                navigateToRename = {},
                navigateToHearingProtection = {},
                navigateToHearingAid = {},
                navigateToLeftLongPress = {},
                navigateToRightLongPress = {},
                navigateToPurchase = {},
                navigateToAdaptiveStrength = {},
                navigateToEqualizer = {},
                navigateToHeadTracking = {},
                navigateToAccessibility = {},
                navigateToVersion = {},
                navigateToTroubleshooting = {},
                navigateToCallControlScreen = {},
                navigateToMicrophoneSettings = {},
                activateDemoMode = {},
                reconnectFromSavedMac = {}
            )
        }
    }

    @Test
    fun homeLight() = capture("home_light") { Home(demoState) }

    @Test
    @Config(qualifiers = "+night")
    fun homeDark() = capture("home_dark", dark = true) { Home(demoState) }

    @Test
    fun homeNotPremiumLight() = capture("home_free_light") { Home(demoState.copy(isPremium = false)) }

    @Test
    @Config(qualifiers = "w412dp-h2600dp-xhdpi")
    fun homeFullLight() = capture("home_full_light") { Home(demoState) }

    @Test
    @Config(qualifiers = "w412dp-h2600dp-night-xhdpi")
    fun homeFullDark() = capture("home_full_dark", dark = true) { Home(demoState) }

    @Test
    fun disconnectedLight() = capture("disconnected_light") {
        Home(AirPodsUiState(connectionSuccessful = true))
    }

    @Test
    @Config(qualifiers = "+night")
    fun notConnectedDark() = capture("not_connected_dark", dark = true) {
        Home(AirPodsUiState(connectionSuccessful = false))
    }
}
