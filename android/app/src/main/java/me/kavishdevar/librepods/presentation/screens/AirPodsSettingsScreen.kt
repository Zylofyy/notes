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

@file:OptIn(ExperimentalEncodingApi::class)

package me.kavishdevar.librepods.presentation.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Accessibility
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.BluetoothDisabled
import androidx.compose.material.icons.automirrored.rounded.BluetoothSearching
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Gesture
import androidx.compose.material.icons.rounded.HeadsetOff
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Troubleshoot
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.material3.toPath
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.graphics.shapes.Morph
import kotlinx.coroutines.delay
import me.kavishdevar.librepods.BuildConfig
import me.kavishdevar.librepods.R
import me.kavishdevar.librepods.bluetooth.AACPManager
import me.kavishdevar.librepods.bluetooth.ATTHandles
import me.kavishdevar.librepods.data.AirPodsPro3
import me.kavishdevar.librepods.data.Capability
import me.kavishdevar.librepods.presentation.components.AboutCard
import me.kavishdevar.librepods.presentation.components.AudioSettings
import me.kavishdevar.librepods.presentation.components.BatteryView
import me.kavishdevar.librepods.presentation.components.CallControlSettings
import me.kavishdevar.librepods.presentation.components.ConnectionSettings
import me.kavishdevar.librepods.presentation.components.HearingHealthSettings
import me.kavishdevar.librepods.presentation.components.NoiseControlSettings
import me.kavishdevar.librepods.presentation.components.PremiumBanner
import me.kavishdevar.librepods.presentation.components.PressAndHoldSettings
import me.kavishdevar.librepods.presentation.components.StyledButton
import me.kavishdevar.librepods.presentation.components.StyledList
import me.kavishdevar.librepods.presentation.components.StyledListItem
import me.kavishdevar.librepods.presentation.components.StyledToggle
import me.kavishdevar.librepods.presentation.theme.LibrePodsTheme
import me.kavishdevar.librepods.presentation.viewmodel.AirPodsUiState
import me.kavishdevar.librepods.presentation.viewmodel.AirPodsViewModel
import me.kavishdevar.librepods.presentation.viewmodel.demoState
import java.util.concurrent.TimeUnit
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.min
import kotlin.time.Duration.Companion.seconds

@Composable
fun AirPodsSettingsRoute(
    viewModel: AirPodsViewModel,
    navigateToRename: () -> Unit,
    navigateToHearingProtection: () -> Unit,
    navigateToHearingAid: () -> Unit,
    navigateToLeftLongPress: () -> Unit,
    navigateToRightLongPress: () -> Unit,
    navigateToPurchase: () -> Unit,
    navigateToAdaptiveStrength: () -> Unit,
    navigateToEqualizer: () -> Unit,
    navigateToHeadTracking: () -> Unit,
    navigateToAccessibility: () -> Unit,
    navigateToVersion: () -> Unit,
    navigateToTroubleshooting: () -> Unit,
    navigateToCallControlScreen: (action: String) -> Unit,
    navigateToMicrophoneSettings: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    AirPodsSettingsScreen(
        state = state,

        setControlCommandInt = viewModel::setControlCommandInt,
        setControlCommandBoolean = viewModel::setControlCommandBoolean,
        setControlCommandByte = viewModel::setControlCommandByte,
        setATTCharacteristicValue = viewModel::setATTCharacteristicValue,

        onAutomaticEarDetectionChanged = viewModel::setAutomaticEarDetectionEnabled,
        onAutomaticConnectionChanged = viewModel::setAutomaticConnectionEnabled,
        setDynamicEndOfCharge = viewModel::setDynamicEndOfCharge,
        setOffListeningMode = viewModel::setOffListeningMode,
        disconnect = viewModel::disconnect,

        navigateToRename = navigateToRename,
        navigateToHearingProtection = navigateToHearingProtection,
        navigateToHearingAid = navigateToHearingAid,
        navigateToLeftLongPress = navigateToLeftLongPress,
        navigateToRightLongPress = navigateToRightLongPress,
        navigateToPurchase = navigateToPurchase,
        navigateToAdaptiveStrength = navigateToAdaptiveStrength,
        navigateToEqualizer = navigateToEqualizer,
        navigateToHeadTracking = navigateToHeadTracking,
        navigateToAccessibility = navigateToAccessibility,
        navigateToVersion = navigateToVersion,
        navigateToTroubleshooting = navigateToTroubleshooting,
        navigateToCallControlScreen = navigateToCallControlScreen,
        navigateToMicrophoneSettings = navigateToMicrophoneSettings,

        activateDemoMode = viewModel::activateDemoMode,
        reconnectFromSavedMac = viewModel::reconnectFromSavedMac
    )
}

@SuppressLint("MissingPermission", "UnspecifiedRegisterReceiverFlag")
@Composable
fun AirPodsSettingsScreen(
    state: AirPodsUiState,

    setControlCommandInt: (AACPManager.Companion.ControlCommandIdentifiers, Int) -> Unit,
    setControlCommandBoolean: (AACPManager.Companion.ControlCommandIdentifiers, Boolean) -> Unit,
    setControlCommandByte: (AACPManager.Companion.ControlCommandIdentifiers, Byte) -> Unit,
    setATTCharacteristicValue: (ATTHandles, ByteArray) -> Unit,

    onAutomaticEarDetectionChanged: (Boolean) -> Unit,
    onAutomaticConnectionChanged: (Boolean) -> Unit,
    setDynamicEndOfCharge: (Boolean) -> Unit,
    setOffListeningMode: (Boolean) -> Unit,
    disconnect: () -> Unit,

    navigateToRename: () -> Unit,
    navigateToHearingProtection: () -> Unit,
    navigateToHearingAid: () -> Unit,
    navigateToLeftLongPress: () -> Unit,
    navigateToRightLongPress: () -> Unit,
    navigateToPurchase: () -> Unit,
    navigateToAdaptiveStrength: () -> Unit,
    navigateToEqualizer: () -> Unit,
    navigateToHeadTracking: () -> Unit,
    navigateToAccessibility: () -> Unit,
    navigateToVersion: () -> Unit,
    navigateToTroubleshooting: () -> Unit,
    navigateToCallControlScreen: (action: String) -> Unit,
    navigateToMicrophoneSettings: () -> Unit,

    activateDemoMode: () -> Unit,
    reconnectFromSavedMac: () -> Unit,
) {
    if (state.isLocallyConnected) {
        ConnectedContent(
            state = state,
            setControlCommandInt = setControlCommandInt,
            setControlCommandBoolean = setControlCommandBoolean,
            setATTCharacteristicValue = setATTCharacteristicValue,
            onAutomaticEarDetectionChanged = onAutomaticEarDetectionChanged,
            onAutomaticConnectionChanged = onAutomaticConnectionChanged,
            setDynamicEndOfCharge = setDynamicEndOfCharge,
            setOffListeningMode = setOffListeningMode,
            disconnect = disconnect,
            navigateToRename = navigateToRename,
            navigateToHearingProtection = navigateToHearingProtection,
            navigateToHearingAid = navigateToHearingAid,
            navigateToLeftLongPress = navigateToLeftLongPress,
            navigateToRightLongPress = navigateToRightLongPress,
            navigateToPurchase = navigateToPurchase,
            navigateToAdaptiveStrength = navigateToAdaptiveStrength,
            navigateToEqualizer = navigateToEqualizer,
            navigateToHeadTracking = navigateToHeadTracking,
            navigateToAccessibility = navigateToAccessibility,
            navigateToVersion = navigateToVersion,
            navigateToCallControlScreen = navigateToCallControlScreen,
            navigateToMicrophoneSettings = navigateToMicrophoneSettings,
        )
    } else {
        DisconnectedContent(
            connectionSuccessful = state.connectionSuccessful,
            navigateToTroubleshooting = navigateToTroubleshooting,
            activateDemoMode = activateDemoMode,
            reconnectFromSavedMac = reconnectFromSavedMac,
        )
    }
}

@Composable
private fun rememberDeviceName(fallback: String): String {
    val sharedPreferences = LocalContext.current.getSharedPreferences("settings", MODE_PRIVATE)
    var deviceName by remember { mutableStateOf(sharedPreferences.getString("name", fallback) ?: fallback) }
    DisposableEffect(sharedPreferences) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == "name") deviceName = prefs.getString("name", fallback) ?: fallback
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        onDispose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }
    return deviceName
}

@Composable
private fun ConnectedContent(
    state: AirPodsUiState,
    setControlCommandInt: (AACPManager.Companion.ControlCommandIdentifiers, Int) -> Unit,
    setControlCommandBoolean: (AACPManager.Companion.ControlCommandIdentifiers, Boolean) -> Unit,
    setATTCharacteristicValue: (ATTHandles, ByteArray) -> Unit,
    onAutomaticEarDetectionChanged: (Boolean) -> Unit,
    onAutomaticConnectionChanged: (Boolean) -> Unit,
    setDynamicEndOfCharge: (Boolean) -> Unit,
    setOffListeningMode: (Boolean) -> Unit,
    disconnect: () -> Unit,
    navigateToRename: () -> Unit,
    navigateToHearingProtection: () -> Unit,
    navigateToHearingAid: () -> Unit,
    navigateToLeftLongPress: () -> Unit,
    navigateToRightLongPress: () -> Unit,
    navigateToPurchase: () -> Unit,
    navigateToAdaptiveStrength: () -> Unit,
    navigateToEqualizer: () -> Unit,
    navigateToHeadTracking: () -> Unit,
    navigateToAccessibility: () -> Unit,
    navigateToVersion: () -> Unit,
    navigateToCallControlScreen: (action: String) -> Unit,
    navigateToMicrophoneSettings: () -> Unit,
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("settings", MODE_PRIVATE)
    val deviceName = rememberDeviceName(state.deviceName)
    val capabilities = state.capabilities
    val model = state.instance?.model
    val controls = state.controlStates

    fun flag(id: AACPManager.Companion.ControlCommandIdentifiers) = controls[id]?.getOrNull(0) == 0x01.toByte()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (state.timeUntilFOSSPremiumExpiry > 0L) {
            item(key = "play_update_banner") {
                Surface(
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_SENDTO).apply {
                            data = "mailto:".toUri()
                            putExtra(Intent.EXTRA_EMAIL, arrayOf("billing@kavish.xyz"))
                            putExtra(Intent.EXTRA_SUBJECT, "LibrePods Play billing error")
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Please enter your GitHub username to restore your premium access:\n\nGitHub username: "
                            )
                        })
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(Icons.Rounded.Info, contentDescription = null)
                        Text(
                            text = stringResource(
                                R.string.play_foss_premium_banner,
                                maxOf(1, TimeUnit.MILLISECONDS.toDays(state.timeUntilFOSSPremiumExpiry).toInt())
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        item(key = "battery") {
            BatteryView(
                batteryList = state.battery,
                budsRes = model?.budsRes ?: R.drawable.airpods_pro_2_buds,
                caseRes = model?.caseRes ?: R.drawable.airpods_pro_2_case
            )
        }

        if (capabilities.contains(Capability.LISTENING_MODE)) {
            item(key = "listening_mode") {
                NoiseControlSettings(
                    showOffListeningMode = state.offListeningMode,
                    noiseControlModeValue = controls[AACPManager.Companion.ControlCommandIdentifiers.LISTENING_MODE]
                        ?.getOrNull(0)?.toInt() ?: 3,
                    onNoiseControlModeChanged = {
                        setControlCommandInt(AACPManager.Companion.ControlCommandIdentifiers.LISTENING_MODE, it)
                    },
                )
            }
        }

        if (!state.isPremium) {
            item(key = "upgrade") { PremiumBanner(onClick = navigateToPurchase) }
        }

        item(key = "name") {
            StyledListItem(
                name = stringResource(R.string.device_name),
                description = deviceName,
                onClick = navigateToRename,
                icon = Icons.Rounded.Edit
            )
        }

        val hasHearingAid = model?.capabilities?.contains(Capability.HEARING_AID) == true
        val hasPPE = model?.capabilities?.contains(Capability.PPE) == true
        if (hasPPE || (hasHearingAid && state.vendorIdHook)) {
            item(key = "hearing_health") {
                HearingHealthSettings(
                    hasPPECapability = hasPPE,
                    hasHearingAidCapability = hasHearingAid,
                    vendorIdHook = state.vendorIdHook,
                    navigateToHearingProtection = navigateToHearingProtection,
                    navigateToHearingAid = navigateToHearingAid
                )
            }
        }

        if (capabilities.contains(Capability.STEM_CONFIG)) {
            item(key = "press_hold") {
                PressAndHoldSettings(
                    leftAction = state.leftAction,
                    rightAction = state.rightAction,
                    navigateToLeftLongPress = navigateToLeftLongPress,
                    navigateToRightLongPress = navigateToRightLongPress
                )
            }
        }

        item(key = "audio") {
            val audioModel = model ?: AirPodsPro3()
            AudioSettings(
                adaptiveVolumeCapability = audioModel.capabilities.contains(Capability.ADAPTIVE_VOLUME),
                conversationAwarenessCapability = audioModel.capabilities.contains(Capability.CONVERSATION_AWARENESS),
                loudSoundReductionCapability = audioModel.capabilities.contains(Capability.LOUD_SOUND_REDUCTION),
                adaptiveAudioCapability = audioModel.capabilities.contains(Capability.ADAPTIVE_VOLUME),
                customEqCapability = true,
                adaptiveVolumeChecked = flag(AACPManager.Companion.ControlCommandIdentifiers.ADAPTIVE_VOLUME_CONFIG),
                onAdaptiveVolumeCheckedChange = {
                    setControlCommandBoolean(AACPManager.Companion.ControlCommandIdentifiers.ADAPTIVE_VOLUME_CONFIG, it)
                },
                conversationAwarenessChecked = flag(AACPManager.Companion.ControlCommandIdentifiers.CONVERSATION_DETECT_CONFIG) && state.isPremium,
                onConversationAwarenessCheckedChange = {
                    setControlCommandBoolean(AACPManager.Companion.ControlCommandIdentifiers.CONVERSATION_DETECT_CONFIG, it)
                },
                loudSoundReductionChecked = state.loudSoundReductionEnabled,
                onLoudSoundReductionCheckedChange = { checked ->
                    setATTCharacteristicValue(
                        ATTHandles.LOUD_SOUND_REDUCTION,
                        byteArrayOf(if (checked) 0x01 else 0x00)
                    )
                },
                navigateToAdaptiveStrength = navigateToAdaptiveStrength,
                navigateToEqualizer = navigateToEqualizer,
                vendorIdHook = state.vendorIdHook,
                isPremium = state.isPremium
            )
        }

        item(key = "call_control") {
            val bytes = controls[AACPManager.Companion.ControlCommandIdentifiers.CALL_MANAGEMENT_CONFIG]
                ?.take(2)?.toByteArray() ?: byteArrayOf(0x00, 0x00)
            CallControlSettings(
                flipped = bytes.getOrNull(1) == 0x02.toByte(),
                navigateToCallControlScreen = navigateToCallControlScreen
            )
        }

        item(key = "connection") {
            ConnectionSettings(
                automaticEarDetectionEnabled = state.automaticEarDetectionEnabled,
                onAutomaticEarDetectionChanged = onAutomaticEarDetectionChanged,
                automaticConnectionEnabled = state.automaticConnectionEnabled,
                onAutomaticConnectionChanged = onAutomaticConnectionChanged
            )
        }

        item(key = "controls") {
            val micMode = when (controls[AACPManager.Companion.ControlCommandIdentifiers.MIC_MODE]?.getOrNull(0)) {
                0x01.toByte() -> stringResource(R.string.microphone_always_right)
                0x02.toByte() -> stringResource(R.string.microphone_always_left)
                else -> stringResource(R.string.microphone_automatic)
            }
            StyledList(title = stringResource(R.string.controls)) {
                StyledListItem(
                    name = stringResource(R.string.microphone_mode),
                    description = micMode,
                    onClick = navigateToMicrophoneSettings,
                    icon = Icons.Rounded.Mic
                )
                if (capabilities.contains(Capability.HEAD_GESTURES)) {
                    StyledListItem(
                        name = stringResource(R.string.head_gestures),
                        description = stringResource(
                            if (sharedPreferences.getBoolean("head_gestures", false)) R.string.on else R.string.off
                        ),
                        onClick = navigateToHeadTracking,
                        icon = Icons.Rounded.Gesture
                    )
                }
                if (capabilities.contains(Capability.LOUD_SOUND_REDUCTION)) {
                    StyledToggle(
                        label = stringResource(R.string.off_listening_mode),
                        description = stringResource(R.string.off_listening_mode_description),
                        checked = flag(AACPManager.Companion.ControlCommandIdentifiers.ALLOW_OFF_OPTION),
                        onCheckedChange = setOffListeningMode,
                        icon = Icons.Rounded.HeadsetOff
                    )
                }
                StyledListItem(
                    name = stringResource(R.string.accessibility),
                    onClick = navigateToAccessibility,
                    icon = Icons.Rounded.Accessibility
                )
            }
        }

        item(key = "battery_sleep") {
            StyledList(
                title = stringResource(R.string.battery_and_charging),
                description = stringResource(R.string.optimized_charging_description)
            ) {
                StyledToggle(
                    label = stringResource(R.string.optimized_charging),
                    checked = state.dynamicEndOfCharge,
                    onCheckedChange = setDynamicEndOfCharge,
                    icon = Icons.Rounded.BatteryChargingFull
                )
                if (capabilities.contains(Capability.SLEEP_DETECTION)) {
                    val id = AACPManager.Companion.ControlCommandIdentifiers.SLEEP_DETECTION_CONFIG
                    StyledToggle(
                        label = stringResource(R.string.sleep_detection),
                        checked = flag(id),
                        onCheckedChange = { setControlCommandBoolean(id, it) },
                        enabled = state.isPremium,
                        icon = Icons.Rounded.Bedtime
                    )
                }
            }
        }

        item(key = "about") {
            AboutCard(
                modelName = state.modelName,
                actualModel = state.actualModel,
                serialNumbers = state.serialNumbers,
                version = state.version3,
                navigateToVersion = navigateToVersion
            )
        }

        item(key = "disconnect") {
            StyledButton(onClick = disconnect, modifier = Modifier.fillMaxWidth()) {
                Icon(
                    Icons.Rounded.BluetoothDisabled,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.iconSizeFor(ButtonDefaults.MediumContainerHeight))
                )
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.disconnect))
            }
        }
    }
}

/**
 * Not-connected state. The big tonal shape is the reconnect button: it squishes into
 * the next Material shape while pressed and keeps morphing through the shape set
 * while a reconnect is in progress.
 */
@Composable
private fun DisconnectedContent(
    connectionSuccessful: Boolean,
    navigateToTroubleshooting: () -> Unit,
    activateDemoMode: () -> Unit,
    reconnectFromSavedMac: () -> Unit,
) {
    val sharedPreferences = LocalContext.current.getSharedPreferences("settings", MODE_PRIVATE)
    val tapCount = remember { mutableIntStateOf(0) }
    val lastTapTime = remember { mutableLongStateOf(0L) }
    var reconnecting by remember { mutableStateOf(false) }

    LaunchedEffect(reconnecting) {
        if (reconnecting) {
            delay(5.seconds)
            reconnecting = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onLongPress = { activateDemoMode() }) }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp)
                .pointerInput(Unit) {
                    // Five quick taps on the message also start demo mode.
                    detectTapGestures(onTap = {
                        val now = System.currentTimeMillis()
                        if (now - lastTapTime.longValue > 400) tapCount.intValue = 0
                        tapCount.intValue++
                        lastTapTime.longValue = now
                        if (tapCount.intValue >= 5) {
                            tapCount.intValue = 0
                            activateDemoMode()
                        }
                    })
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (connectionSuccessful) {
                MorphingReconnectButton(
                    reconnecting = reconnecting,
                    onReconnect = {
                        reconnecting = true
                        reconnectFromSavedMac()
                    }
                )
                Spacer(Modifier.height(32.dp))
                Text(
                    text = stringResource(if (reconnecting) R.string.reconnecting else R.string.tap_to_reconnect),
                    style = MaterialTheme.typography.titleLargeEmphasized,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.reconnect_to_last_device),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(MaterialShapes.Cookie9Sided.toShape())
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.HeadsetOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(80.dp)
                    )
                }
                Spacer(Modifier.height(32.dp))
                Text(
                    text = stringResource(R.string.airpods_not_connected),
                    style = MaterialTheme.typography.headlineSmallEmphasized,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.airpods_not_connected_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                if (sharedPreferences.getBoolean("bypass_device_check.v2", false)) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.compatibility_check_bypassed),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (!BuildConfig.PLAY_BUILD) {
            OutlinedButton(
                onClick = navigateToTroubleshooting,
                shapes = ButtonDefaults.shapes(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(24.dp)
            ) {
                Icon(Icons.Rounded.Troubleshoot, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.troubleshooting))
            }
        }
    }
}

@Composable
private fun MorphingReconnectButton(reconnecting: Boolean, onReconnect: () -> Unit) {
    val polygons = remember {
        listOf(
            MaterialShapes.Cookie9Sided,
            MaterialShapes.Clover4Leaf,
            MaterialShapes.SoftBurst,
            MaterialShapes.Sunny,
            MaterialShapes.Pentagon,
            MaterialShapes.Cookie4Sided,
            MaterialShapes.Oval,
        )
    }
    val morphs = remember {
        polygons.indices.map { i -> Morph(polygons[i].normalized(), polygons[(i + 1) % polygons.size].normalized()) }
    }
    var morphIndex by remember { mutableIntStateOf(0) }
    val morphProgress = remember { Animatable(0f) }
    val motion = MaterialTheme.motionScheme

    LaunchedEffect(reconnecting) {
        if (!reconnecting) {
            morphIndex = 0
            morphProgress.snapTo(0f)
            return@LaunchedEffect
        }
        while (reconnecting) {
            morphProgress.snapTo(0f)
            morphProgress.animateTo(1f, motion.slowSpatialSpec())
            morphIndex = (morphIndex + 1) % morphs.size
        }
    }

    val shapeColor by animateColorAsState(
        if (reconnecting) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer,
        label = "reconnectColor"
    )
    val iconColor = if (reconnecting) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
    val path = remember { Path() }
    val matrix = remember { Matrix() }
    val press = spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)

    Box(
        modifier = Modifier
            .size(240.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = null,
                indication = ripple(bounded = false, radius = 120.dp),
                enabled = !reconnecting,
                onClick = {}
            )
            .pointerInput(reconnecting) {
                detectTapGestures(
                    onTap = { if (!reconnecting) { morphIndex = 1; onReconnect() } },
                    onPress = {
                        if (!reconnecting) {
                            morphProgress.animateTo(1f, press)
                            tryAwaitRelease()
                            morphProgress.animateTo(0f, press)
                        }
                    }
                )
            }
            .drawWithContent {
                val shapePath = morphs[morphIndex].toPath(progress = morphProgress.value, path = path)
                val bounds = shapePath.getBounds()
                val scale = min(size.width / bounds.width, size.height / bounds.height) * 0.85f
                matrix.reset()
                matrix.scale(x = scale, y = scale)
                shapePath.transform(matrix)
                shapePath.translate(size.center - shapePath.getBounds().center)
                drawPath(path = shapePath, color = shapeColor)
                drawContent()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (reconnecting) Icons.AutoMirrored.Rounded.BluetoothSearching else Icons.Rounded.HeadsetOff,
            contentDescription = stringResource(R.string.tap_to_reconnect),
            modifier = Modifier.size(84.dp),
            tint = iconColor
        )
    }
}

@Preview(name = "Connected")
@Composable
fun AirPodsSettingsScreenPreview() {
    LibrePodsTheme(dynamicColor = false) {
        Box(Modifier.background(MaterialTheme.colorScheme.surfaceContainer)) {
            AirPodsSettingsScreen(
                state = demoState,
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
}
