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

package me.kavishdevar.librepods.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.res.stringResource
import me.kavishdevar.librepods.R
import me.kavishdevar.librepods.presentation.components.AppScaffold
import me.kavishdevar.librepods.presentation.viewmodel.AirPodsViewModel

@Composable
fun NavigationRoot(
    showReleaseNotes: Boolean = false,
    updatesShown: () -> Unit = {},
    showOnboarding: Boolean = false,
    onboardingComplete: () -> Unit = {},
    airPodsViewModel: AirPodsViewModel
) {
    val backStack = remember {
        mutableStateListOf(
            when {
                showOnboarding -> Screen.Onboarding
                showReleaseNotes -> Screen.ReleaseNotes
                else -> Screen.AirPodsSettings
            }
        )
    }

    AppNavGraph(
        showReleaseNotes = showReleaseNotes,
        updatesShown = updatesShown,
        showOnboarding = showOnboarding,
        onboardingComplete = onboardingComplete,
        backStack = backStack,
        airPodsViewModel = airPodsViewModel,
    )
}

/** Wraps a destination in its own [AppScaffold] so each screen owns a collapsing top bar. */
@Composable
internal fun ScreenFrame(
    screen: Screen,
    backStack: SnapshotStateList<Screen>,
    airPodsViewModel: AirPodsViewModel,
    content: @Composable () -> Unit
) {
    val state by airPodsViewModel.uiState.collectAsState()

    val title = when (screen) {
        Screen.Onboarding -> ""
        Screen.AirPodsSettings -> if (state.isLocallyConnected) state.deviceName else stringResource(R.string.app_name)
        Screen.Accessibility -> stringResource(R.string.accessibility)
        Screen.AdaptiveStrength -> stringResource(R.string.customize_adaptive_audio)
        Screen.AppSettings -> stringResource(R.string.settings)
        Screen.Equalizer -> stringResource(R.string.equalizer)
        Screen.HeadTracking -> stringResource(R.string.head_tracking)
        Screen.HearingAid -> stringResource(R.string.hearing_aid)
        Screen.HearingAidAdjustments -> stringResource(R.string.adjustments)
        Screen.HearingProtection -> stringResource(R.string.hearing_protection)
        is Screen.LongPress -> screen.bud
        Screen.OpenSourceLicenses -> stringResource(R.string.open_source_licenses)
        Screen.Purchase -> stringResource(R.string.unlock_advanced_features)
        Screen.Rename -> stringResource(R.string.name)
        Screen.TransparencyCustomization -> stringResource(R.string.customize_transparency_mode)
        Screen.Troubleshooting -> stringResource(R.string.troubleshooting)
        Screen.UpdateHearingTest -> stringResource(R.string.update_hearing_test)
        Screen.VersionInfo -> stringResource(R.string.version)
        is Screen.CallControl -> screen.action
        Screen.MicrophoneSettings -> stringResource(R.string.microphone_mode)
        Screen.ReleaseNotes -> ""
    }

    val subtitle = when (screen) {
        Screen.AirPodsSettings -> if (state.isLocallyConnected) stringResource(R.string.connected) else null
        else -> null
    }

    val isRoot = backStack.indexOf(screen) <= 0

    AppScaffold(
        title = title,
        subtitle = subtitle,
        showTopBar = screen.showTopBar,
        showBackButton = !isRoot,
        onNavigateBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
        actions = {
            when (screen) {
                Screen.AirPodsSettings -> FilledTonalIconButton(
                    onClick = { backStack.add(Screen.AppSettings) },
                    shapes = IconButtonDefaults.shapes(),
                ) {
                    Icon(Icons.Rounded.Settings, contentDescription = stringResource(R.string.settings))
                }

                Screen.HeadTracking -> FilledTonalIconToggleButton(
                    checked = state.headTrackingActive,
                    onCheckedChange = {
                        if (it) airPodsViewModel.startHeadTracking() else airPodsViewModel.stopHeadTracking()
                    },
                    shapes = IconButtonDefaults.toggleableShapes(),
                ) {
                    Icon(
                        imageVector = if (state.headTrackingActive) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = stringResource(
                            if (state.headTrackingActive) R.string.stop_head_tracking else R.string.start_head_tracking
                        )
                    )
                }

                else -> Unit
            }
        },
        content = content
    )
}
